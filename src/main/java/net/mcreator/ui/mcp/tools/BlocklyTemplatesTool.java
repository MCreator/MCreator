/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.ui.mcp.tools;

import net.mcreator.blockly.BlocklyTemplateIO;
import net.mcreator.io.ResourcePointer;
import net.mcreator.io.TemplatesLoader;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BlocklyTemplatesTool extends MCreatorMcpTool<BlocklyTemplatesTool.Args> {

	public static class Args {
		public QueryType type;
		@Nullable public String blocklyEditorType;
		@Nullable public String templateName;

		public enum QueryType {
			LIST_TEMPLATES, GET_TEMPLATE
		}
	}

	public BlocklyTemplatesTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "blockly_templates";
	}

	@Override public String getDescription() {
		return """
				Lists Blockly template/example names for blocklyEditorType %s or returns full Blockly XML for templateName.\
				Good to discover example block assemblies before building custom Blockly setups. Some fields and inputs may not be filled out.\
				GET_TEMPLATE returns Blockly XML of the template; feature templates may contain a feature block, placement blocks, or both.
				""".formatted(getTemplateSupportedEditorTypes());
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		return switch (input.type) {
			case LIST_TEMPLATES -> {
				BlocklyEditorType editorType = parseEditorType(input.blocklyEditorType);
				if (editorType == null) {
					yield completedError("Invalid or missing blocklyEditorType");
				}
				if (editorType.extension() == null) {
					yield completedError("blocklyEditorType does not support templates: " + editorType.registryName());
				}
				yield completed(ToolResult.collection(loadTemplates(editorType).stream().map(ResourcePointer::toString)
						.sorted(Comparator.naturalOrder()).toList()));
			}
			case GET_TEMPLATE -> {
				if (input.templateName == null || input.templateName.isBlank()) {
					yield completedError("templateName is required");
				}
				BlocklyEditorType editorType = parseEditorType(input.blocklyEditorType);
				if (editorType == null) {
					yield completedError("Invalid or missing blocklyEditorType");
				}
				if (editorType.extension() == null) {
					yield completedError("blocklyEditorType does not support templates: " + editorType.registryName());
				}
				ResourcePointer template = findTemplate(editorType, input.templateName.trim());
				if (template == null) {
					yield completedError("Unknown template: " + input.templateName);
				}
				try {
					String templateXml = template.identifier instanceof String resourcePath ?
							BlocklyTemplateIO.importBlocklyXML("/" + resourcePath) :
							BlocklyTemplateIO.importBlocklyXML((File) template.identifier);
					yield completedText(wrapTemplateWithStartBlock(templateXml, editorType));
				} catch (Exception e) {
					yield completedError("Failed to load template: " + e.getMessage(), e);
				}
			}
		};
	}

	@Nullable private static BlocklyEditorType parseEditorType(@Nullable String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		return BlocklyEditorType.fromName(raw.trim());
	}

	private static List<ResourcePointer> loadTemplates(BlocklyEditorType editorType) {
		return TemplatesLoader.loadTemplates(editorType.extension(), editorType.extension());
	}

	@Nullable private static ResourcePointer findTemplate(BlocklyEditorType editorType, String templateName) {
		for (ResourcePointer template : loadTemplates(editorType)) {
			if (template.toString().equalsIgnoreCase(templateName)) {
				return template;
			}
		}
		return null;
	}

	private static List<String> getTemplateSupportedEditorTypes() {
		return BlocklyEditorType.getTypes().stream().map(BlocklyEditorType::fromName)
				.filter(type -> type.extension() != null).map(BlocklyEditorType::registryName).sorted().toList();
	}

	private static String wrapTemplateWithStartBlock(String templateXml, BlocklyEditorType editorType)
			throws ParserConfigurationException, IOException, SAXException {
		String trimmed = templateXml.trim();
		if (trimmed.contains("type=\"" + editorType.startBlockName() + "\"")) {
			return trimmed;
		}

		String blocksXml = trimmed.startsWith("<xml") ? stripXmlWrapper(trimmed) : trimmed;

		if (editorType == BlocklyEditorType.FEATURE) {
			return wrapFeatureTemplate(trimmed);
		}

		return wrapWithStartBlock(editorType, null, blocksXml);
	}

	private static String stripXmlWrapper(String templateXml) {
		int xmlStart = templateXml.indexOf('>');
		if (xmlStart < 0) {
			return templateXml;
		}
		int xmlEnd = templateXml.lastIndexOf("</xml>");
		if (xmlEnd < 0) {
			return templateXml.substring(xmlStart + 1).trim();
		}
		return templateXml.substring(xmlStart + 1, xmlEnd).trim();
	}

	private static String wrapFeatureTemplate(String blocksXml)
			throws ParserConfigurationException, IOException, SAXException {
		Document doc = parseDocument(blocksXml.startsWith("<xml") ? blocksXml : "<xml>" + blocksXml + "</xml>");
		@Nullable String featureBlockXml = null;
		@Nullable String proceduralXml = null;
		for (Element block : XMLUtil.getDirectChildren(doc.getDocumentElement())) {
			if (!"block".equals(block.getNodeName())) {
				continue;
			}
			String blockXml = serializeElement(block);
			if (isFeatureBlockType(block.getAttribute("type"))) {
				featureBlockXml = blockXml;
			} else {
				proceduralXml = blockXml;
			}
		}
		return wrapWithStartBlock(BlocklyEditorType.FEATURE, featureBlockXml, proceduralXml);
	}

	private static Document parseDocument(String xml) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newDefaultInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		doc.getDocumentElement().normalize();
		return doc;
	}

	private static String serializeElement(Element element) {
		DOMImplementationLS lsImpl = (DOMImplementationLS) element.getOwnerDocument().getImplementation()
				.getFeature("LS", "3.0");
		LSSerializer serializer = lsImpl.createLSSerializer();
		serializer.getDomConfig().setParameter("xml-declaration", false);
		return serializer.writeToString(element).replaceAll("[\n\r\t]", "");
	}

	private static boolean isFeatureBlockType(String blockType) {
		return blockType.startsWith("feature_");
	}

	private static String wrapWithStartBlock(BlocklyEditorType editorType, @Nullable String featureBlockXml,
			@Nullable String proceduralXml) {
		StringBuilder setupXml = new StringBuilder("<xml xmlns=\"https://developers.google.com/blockly/xml\">");
		setupXml.append(getStartBlockOpenTag(editorType));
		if (featureBlockXml != null) {
			setupXml.append("<value name=\"feature\">").append(featureBlockXml).append("</value>");
		}
		if (proceduralXml != null) {
			setupXml.append("<next>").append(proceduralXml).append("</next>");
		}
		setupXml.append("</block></xml>");
		return setupXml.toString();
	}

	private static String getStartBlockOpenTag(BlocklyEditorType editorType) {
		if (editorType == BlocklyEditorType.PROCEDURE) {
			return "<block type=\"event_trigger\" deletable=\"false\" x=\"40\" y=\"40\"><field name=\"trigger\">no_ext_trigger</field>";
		} else if (editorType == BlocklyEditorType.SCRIPT) {
			return "<block type=\"script_trigger\" deletable=\"false\" x=\"40\" y=\"40\"><field name=\"trigger\">no_ext_trigger</field>";
		} else {
			return "<block type=\"" + editorType.startBlockName() + "\" deletable=\"false\" x=\"40\" y=\"40\">";
		}
	}

}