/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.ui.dialogs;

import net.mcreator.preferences.PreferencesManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * e-mail: 3154934427@qq.com
 *
 * @author cdc123
 * @classname RulesDialog
 * @date 2022/8/17 7:17
 */
public class RulesDialog {
	public static void main(String[] args){
		new RulesDialog(null);
	}

	private final String text = """
			<html>
			<body><div align="center"><h1 id='mcreator模组开发须知'>MCreator模组开发须知</h1></div>
			<h4 id='在你要开始学习使用mcreator制作模组前请仔细阅读以下内容'>在你要开始学习使用MCreator制作模组前，请仔细阅读以下内容：</h4>
			<p>&nbsp;</p>
			<p>1 MCreator制作的模组最好不用于任何形式（除玩家自愿资助，但不得逼迫或诱导玩家）的盈利。</p>
			<p>2 MCreator制作的模组请标注（包括但不限于模组信息Author，发布时的声明等），且不允许将它伪造成自己编写的模组或拒绝承认其是由MCreator制作的。</p>
			<p>3 MCreator不得用于制作包括以下任何之一类型的模组：无意义秒杀，外挂，恶意或无意义玩梗，仅添加少量物品/方块/工具装备/低质量维度，材质过差，过度依赖自动生成模板材质的模组。</p>
			<p>4严禁未经允许重制其他人的模组，严禁抄袭模组，严禁直接复制材质和代码。</p>
			<p>5严禁出现刷热度，刷好评等违规情况。</p>
			<p>6严禁将本模组某些事态扩散（如评论区骂战，作者个人问题等）。</p>
			<p>7不应在完成度极低/质量极差/违反规定的情况下发布模组至平台（如mcmod，mcbbs，curseforge等）。</p>
			<p>8 MCreator有局限性，切勿抱过大的希望。</p>
			<p>9严禁发表对MCreator带有强烈主观的侮辱性/攻击性言论，如“mcr垃圾”，但可以客观评价其对模组圈造成的影响及对其制作模组的评价。</p>
			<p>10对于违反以上规范的模组，可能会出现以下情况：</p>
			<p>①谴责和批评。</p>
			<p>②强制标注是由MCreator制作，或有谴责和批评。</p>
			<p>③或联系发布平台下架，作为反面教材。</p>
			<p>④强制下架或要求下一版本更改。</p>
			<p>⑤一经发现下架，谴责和批评，作为反面教材。</p>
			<p>⑥下架或者关闭评论，作为反面教材，永久踢出MCreator讨论群。</p>
			<p>⑦无处理，但不提倡。</p>
			<p>⑨永久踢出MCreator讨论群。</p>
			<p>&nbsp;</p>
			<h3 id='请确保你已经认真阅读并同意否则所造成的一切后果由个人承担mcreator及其讨论群和任何其他用户不承担责任ps内容非mcreator官方提供由社区编辑'>请确保你已经认真阅读并同意，否则所造成的一切后果由个人承担，MCreator及其讨论群和任何其他用户不承担责任。PS：内容非MCreator官方提供，由社区编辑。</h3>
			<p> </p>
			<p><em>Q：构建失败怎么办？（创建工作区弹窗失败）</em></p>
			<p><em>A：点左一按钮RE-RUN。或者下载一群群文件已构建版本。</em></p>
			<p><em>Q：兼容什么版本？</em></p>
			<p><em>A：JavaForge1.16.5+版本。</em></p>
			<p><em>Q：已构建文件如何解压？</em></p>
			<p><em>A：属于分卷压缩，解压part1自动解压所有。解压至C:\\Users\\你的用户名 下。</em></p>
			<p><em>Q：如何建模？</em></p>
			<p><em>A：使用blockbench。具体教程自行百度。导入json后需要在此界面选择材质才能应用。</em></p>
			<p><em>Q：模组怎么设置作者，版本，官网，描述，名称，modid（模组注册名）等？</em></p>
			<p><em>A：右上角的蓝色长方形，带一个小齿轮。点开即可。</em></p>
			<p><em>Q:这个什么时候可以按同意?</em></p>
			<p><em>A:等待30秒,这次同意后这条消息就不会出现了,请保持耐心</em></p>
			</body>
			</html>
			""";
	JDialog jd ;
	JPanel content;
	public RulesDialog(Frame parent) {
		jd = new JDialog(parent);
		jd.setTitle("开发须知 -- 请等待30秒");
		content = new JPanel(new BorderLayout());
		jd.setContentPane(content);

		//文本内容
		JScrollPane scroll = new JScrollPane();
		JLabel con = new JLabel(text);
		scroll.setViewportView(con);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		content.add(scroll,BorderLayout.CENTER);

		//按钮面板
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton accept = new JButton("同意");
		JButton refuse = new JButton("不同意");

		accept.setEnabled(false);
		Timer timer = new Timer(30*1000,a->accept.setEnabled(true));
		timer.start();
		accept.addActionListener(a->{
			PreferencesManager.PREFERENCES.hidden.acceptRules = true;
			jd.setVisible(false);
			synchronized (RulesDialog.this){
				RulesDialog.this.notifyAll();
			}
		});
		refuse.addActionListener(a->System.exit(-1));


		buttonPanel.add(accept);
		buttonPanel.add(refuse);
		content.add(buttonPanel,BorderLayout.SOUTH);

		jd.pack();
		jd.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		jd.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.exit(-1);
			}
		});
		jd.setLocationRelativeTo(parent);
		jd.setVisible(true);
	}
}
