Blockly.Blocks['event_trigger'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.event_trigger.line1"));
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.event_trigger.line2"))
            .appendField(new Blockly.FieldDropdown(
                jsonToBlocklyDropDownArray(javabridge.getGlobalTriggers())), 'trigger');
        this.setNextStatement(true);
        this.setStyle('hat_blocks');
        this.setColour(90);
        this.setTooltip(javabridge.t("blockly.block.event_trigger.tooltip"));
    }
};

Blockly.Blocks['cancel_event'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.cancel_event.line1"));
        this.appendDummyInput().appendField(new Blockly.FieldLabel(javabridge.t("blockly.block.cancel_event.line2"), 'small-text'));
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(90);
    }
};

Blockly.Blocks['set_event_result'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.set_event_result.line1"))
        	.appendField(new Blockly.FieldDropdown([["DEFAULT","DEFAULT"], ["ALLOW","ALLOW"], ["DENY","DENY"]]), 'result');
        this.appendDummyInput().appendField(new Blockly.FieldLabel(javabridge.t("blockly.block.set_event_result.line2"), 'small-text'));
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(90);
    }
};

Blockly.Blocks['call_procedure'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.call_procedure"))
            .appendField(new Blockly.FieldDropdown(arrayToBlocklyDropDownArray(javabridge.getListOf("procedure"))), 'procedure');
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(250);
    }
};

Blockly.Blocks['call_procedure_at'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.call_procedure"))
            .appendField(new Blockly.FieldDropdown(arrayToBlocklyDropDownArray(javabridge.getListOf("procedure"))), 'procedure')
            .appendField(javabridge.t("blockly.block.call_procedure.at"));
        this.appendValueInput('x').setCheck('Number').appendField('x: ');
        this.appendValueInput('y').setCheck('Number').appendField('y: ');
        this.appendValueInput('z').setCheck('Number').appendField('z: ');
        this.setInputsInline(true);
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(250);
    }
};

Blockly.Blocks['procedure_retval_logic'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.procedure_retval"))
            .appendField(new Blockly.FieldDropdown(arrayToBlocklyDropDownArray(javabridge.getListOf("procedure_retval_logic"))), 'procedure');
        this.setOutput(true, 'Boolean');
        this.setColour(Blockly.Constants.Logic.HUE);
    }
};

Blockly.Blocks['procedure_retval_number'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.procedure_retval"))
            .appendField(new Blockly.FieldDropdown(arrayToBlocklyDropDownArray(javabridge.getListOf("procedure_retval_number"))), 'procedure');
        this.setOutput(true, 'Number');
        this.setColour(Blockly.Constants.Math.HUE);
    }
};

Blockly.Blocks['procedure_retval_string'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.procedure_retval"))
            .appendField(new Blockly.FieldDropdown(arrayToBlocklyDropDownArray(javabridge.getListOf("procedure_retval_string"))), 'procedure');
        this.setOutput(true, 'String');
        this.setColour(Blockly.Constants.Text.HUE);
    }
};

Blockly.Blocks['procedure_retval_itemstack'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.procedure_retval"))
            .appendField(new Blockly.FieldDropdown(arrayToBlocklyDropDownArray(javabridge.getListOf("procedure_retval_itemstack"))), 'procedure');
        this.setOutput(true, 'MCItem');
        this.setColour(350);
    }
};

Blockly.Blocks['aitasks_container'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.aitasks_container"));
        this.appendDummyInput().appendField(new Blockly.FieldLabel(javabridge.t("blockly.block.aitasks_container.tip"), 'small-text'));
        this.setStyle('hat_blocks');
        this.setNextStatement(true);
        this.setColour(350);
        this.setTooltip(javabridge.t("blockly.block.aitasks_container.tooltip"));
    }
};

Blockly.Blocks['advancement_trigger'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.advancement_trigger"));
        this.setPreviousStatement(true);
        this.setColour(150);
        this.setTooltip(javabridge.t("blockly.block.advancement_trigger.tooltip"));
    }
};

Blockly.Blocks['condition_input'] = {
    init: function () {
        this.appendValueInput('CONDITION').setCheck('Boolean').setAlign(Blockly.ALIGN_RIGHT)
            .appendField(javabridge.t("blockly.block.condition_input"));
        this.setStyle('hat_blocks');
        this.setColour(Blockly.Constants.Logic.HUE);
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setTooltip(javabridge.t("blockly.block.condition_input.tooltip"));
    }
};

Blockly.Blocks['variables_set_logic'] = {
    init: function () {
        this.appendValueInput('VAL').setCheck('Boolean').setAlign(Blockly.ALIGN_RIGHT)
            .appendField(javabridge.t("blockly.block.set_var"))
            .appendField(new Blockly.FieldDropdown(getVariablesOfType("Boolean")), 'VAR')
            .appendField(javabridge.t("blockly.block.set_to"));
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(Blockly.Constants.Logic.HUE);
    }
};

Blockly.Blocks['variables_get_logic'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.get_var"))
            .appendField(new Blockly.FieldDropdown(getVariablesOfType("Boolean")), 'VAR');
        this.setOutput(true, 'Boolean');
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setColour(Blockly.Constants.Logic.HUE);
    }
};

Blockly.Blocks['variables_set_number'] = {
    init: function () {
        this.appendValueInput('VAL').setCheck('Number').setAlign(Blockly.ALIGN_RIGHT)
            .appendField(javabridge.t("blockly.block.set_var"))
            .appendField(new Blockly.FieldDropdown(getVariablesOfType("Number")), 'VAR')
            .appendField(javabridge.t("blockly.block.set_to"));
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(Blockly.Constants.Math.HUE);
    }
};

Blockly.Blocks['variables_get_number'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.get_var"))
            .appendField(new Blockly.FieldDropdown(getVariablesOfType("Number")), 'VAR');
        this.setOutput(true, 'Number');
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setColour(Blockly.Constants.Math.HUE);
    }
};

Blockly.Blocks['variables_set_text'] = {
    init: function () {
        this.appendValueInput('VAL').setCheck('String').setAlign(Blockly.ALIGN_RIGHT)
            .appendField(javabridge.t("blockly.block.set_var"))
            .appendField(new Blockly.FieldDropdown(getVariablesOfType("String")), 'VAR')
            .appendField(javabridge.t("blockly.block.set_to"));
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(Blockly.Constants.Text.HUE);
    }
};

Blockly.Blocks['variables_get_text'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.get_var"))
            .appendField(new Blockly.FieldDropdown(getVariablesOfType("String")), 'VAR');
        this.setOutput(true, 'String');
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setColour(Blockly.Constants.Text.HUE);
    }
};

Blockly.Blocks['variables_set_itemstack'] = {
    init: function () {
        this.appendValueInput('VAL').setCheck('MCItem').setAlign(Blockly.ALIGN_RIGHT)
            .appendField(javabridge.t("blockly.block.set_var"))
            .appendField(new Blockly.FieldDropdown(getVariablesOfType("MCItem")), 'VAR')
            .appendField(javabridge.t("blockly.block.set_to"));
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(350);
    }
};

Blockly.Blocks['variables_get_itemstack'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.get_var"))
            .appendField(new Blockly.FieldDropdown(getVariablesOfType("MCItem")), 'VAR');
        this.setOutput(true, 'MCItem');
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setColour(350);
    }
};

Blockly.Blocks['custom_dependency_logic'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.custom_dependency_logic"))
            .appendField(new Blockly.FieldTextInput("dependencyName"), 'NAME');
        this.setColour(Blockly.Constants.Logic.HUE);
        this.setOutput(true, 'Boolean');
    }
};

Blockly.Blocks['custom_dependency_number'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.custom_dependency_number"))
            .appendField(new Blockly.FieldTextInput("dependencyName"), 'NAME');
        this.setColour(Blockly.Constants.Math.HUE);
        this.setOutput(true, 'Number');
    }
};

Blockly.Blocks['custom_dependency_text'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.custom_dependency_text"))
            .appendField(new Blockly.FieldTextInput("dependencyName"), 'NAME');
        this.setColour(Blockly.Constants.Text.HUE);
        this.setOutput(true, 'String');
    }
};

Blockly.Blocks['custom_dependency_itemstack'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.custom_dependency_itemstack"))
            .appendField(new Blockly.FieldTextInput("dependencyName"), 'NAME');
        this.setColour(350);
        this.setOutput(true, 'MCItem');
    }
};

Blockly.Blocks['java_code'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.java_code"))
            .appendField(new Blockly.FieldTextInput("/*code*/"), 'CODE');
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(250);
    }
};

Blockly.Blocks['java_code_get'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.java_code"))
            .appendField(new Blockly.FieldTextInput("(null)"), 'CODE');
        this.setColour(250);
        this.setOutput(true);
    }
};

Blockly.Blocks['text_contains'] = {
    init: function () {
        this.appendValueInput('text').setCheck('String')
            .appendField(javabridge.t("blockly.block.text_contains.in"));
        this.appendValueInput('contains').setCheck('String')
            .appendField(javabridge.t("blockly.block.text_contains.check"));
        this.setInputsInline(true);
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setOutput(true, 'Boolean');
        this.setColour(Blockly.Constants.Logic.HUE);
    }
};

Blockly.Blocks['text_substring'] = {
    init: function () {
        this.appendValueInput('text').setCheck('String')
            .appendField(javabridge.t("blockly.block.text_substring.substring"));
        this.appendValueInput('from').setCheck('Number')
            .appendField(javabridge.t("blockly.block.text_substring.from"));
        this.appendValueInput('to').setCheck('Number')
            .appendField(javabridge.t("blockly.block.text_substring.to"));
        this.setInputsInline(true);
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setOutput(true, 'String');
        this.setColour(Blockly.Constants.Text.HUE);
    }
};

Blockly.Blocks['text_replace'] = {
    init: function () {
        this.appendValueInput('what').setCheck('String')
            .appendField(javabridge.t("blockly.block.text_replace.replace"));
        this.appendValueInput('with').setCheck('String')
            .appendField(javabridge.t("blockly.block.text_replace.with"));
        this.appendValueInput('text').setCheck('String')
            .appendField(javabridge.t("blockly.block.text_replace.of"));
        this.setInputsInline(true);
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setOutput(true, 'String');
        this.setColour(Blockly.Constants.Text.HUE);
    }
};

Blockly.Blocks['text_format_number'] = {
    init: function () {
        this.appendValueInput('number').setCheck('Number')
            .appendField(javabridge.t("blockly.block.text_format_number.format"));
        this.appendValueInput('format').setCheck('String')
            .appendField(javabridge.t("blockly.block.text_format_number.as"));
        this.setInputsInline(true);
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setOutput(true, 'String');
        this.setColour(Blockly.Constants.Text.HUE);
    }
};

Blockly.Blocks['return_number'] = {
    init: function () {
        this.appendValueInput('return').setCheck('Number').appendField(javabridge.t("blockly.block.return"));
        this.setPreviousStatement(true);
        this.setNextStatement(false);
        this.setColour(Blockly.Constants.Math.HUE);
    }
};

Blockly.Blocks['return_logic'] = {
    init: function () {
        this.appendValueInput('return').setCheck('Boolean').appendField(javabridge.t("blockly.block.return"));
        this.setPreviousStatement(true);
        this.setNextStatement(false);
        this.setColour(Blockly.Constants.Logic.HUE);
    }
};

Blockly.Blocks['return_text'] = {
    init: function () {
        this.appendValueInput('return').setCheck('String').appendField(javabridge.t("blockly.block.return"));
        this.setPreviousStatement(true);
        this.setNextStatement(false);
        this.setColour(Blockly.Constants.Text.HUE);
    }
};

Blockly.Blocks['return_itemstack'] = {
    init: function () {
        this.appendValueInput('return').setCheck('MCItem').appendField(javabridge.t("blockly.block.return"));
        this.setPreviousStatement(true);
        this.setNextStatement(false);
        this.setColour(350);
    }
};

Blockly.defineBlocksWithJsonArray([
    {
        "type": "logic_binary_ops",
        "message0": "%1 %2 %3",
        "args0": [
            {
                "type": "input_value",
                "name": "A",
                "check": "Boolean"
            },
            {
                "type": "field_dropdown",
                "name": "OP",
                "options": [
                    ["=", "EQ"],
                    ["\u2260", "NEQ"],
                    ["AND", "AND"],
                    ["OR", "OR"],
                    ["XOR", "XOR"]
                ]
            },
            {
                "type": "input_value",
                "name": "B",
                "check": "Boolean"
            }
        ],
        "inputsInline": true,
        "output": "Boolean",
        "colour": "%{BKY_LOGIC_HUE}"
    },
    {
        "type": "math_binary_ops",
        "message0": "%1 %2 %3",
        "args0": [
            {
                "type": "input_value",
                "name": "A",
                "check": "Number"
            },
            {
                "type": "field_dropdown",
                "name": "OP",
                "options": [
                    ["=", "EQ"],
                    ["\u2260", "NEQ"],
                    ["<", "LT"],
                    ["\u2264", "LTE"],
                    [">", "GT"],
                    ["\u2265", "GTE"]
                ]
            },
            {
                "type": "input_value",
                "name": "B",
                "check": "Number"
            }
        ],
        "inputsInline": true,
        "output": "Boolean",
        "colour": "%{BKY_MATH_HUE}"
    },
    {
        "type": "text_binary_ops",
        "message0": "%1 = %2",
        "args0": [
            {
                "type": "input_value",
                "name": "A",
                "check": "String"
            },
            {
                "type": "input_value",
                "name": "B",
                "check": "String"
            }
        ],
        "inputsInline": true,
        "output": "Boolean",
        "colour": "%{BKY_TEXTS_HUE}"
    },
    {
        "type": "math_dual_ops",
        "message0": "%1 %2 %3",
        "args0": [
            {
                "type": "input_value",
                "name": "A",
                "check": "Number"
            },
            {
                "type": "field_dropdown",
                "name": "OP",
                "options": [
                    ["+", "ADD"],
                    ["-", "MINUS"],
                    ["*", "MULTIPLY"],
                    ["/", "DIVIDE"],
                    ["^", "POWER"],
                    ["MOD", "MOD"],
                    ["Bitwise AND", "BAND"],
                    ["Bitwise OR", "BOR"],
                    ["Bitwise XOR", "BXOR"],
                    ["Min", "MIN"],
                    ["Max", "MAX"]
                ]
            },
            {
                "type": "input_value",
                "name": "B",
                "check": "Number"
            }
        ],
        "inputsInline": true,
        "output": "Number",
        "colour": "%{BKY_MATH_HUE}"
    },
    {
        "type": "math_singular_ops",
        "message0": "%1 %2",
        "args0": [
            {
                "type": "field_dropdown",
                "name": "OP",
                "options": [
                    ["%{BKY_MATH_ROUND_OPERATOR_ROUND}", "ROUND"],
                    ["%{BKY_MATH_ROUND_OPERATOR_ROUNDUP}", "ROUNDUP"],
                    ["%{BKY_MATH_ROUND_OPERATOR_ROUNDDOWN}", "ROUNDDOWN"],
                    ["%{BKY_MATH_SINGLE_OP_ROOT}", 'ROOT'],
                    ["%{BKY_MATH_SINGLE_OP_ABSOLUTE}", 'ABS'],
                    ['ln', 'LN'],
                    ['log10', 'LOG10'],
                    ["%{BKY_MATH_TRIG_SIN}", "SIN"],
                    ["%{BKY_MATH_TRIG_COS}", "COS"],
                    ["%{BKY_MATH_TRIG_TAN}", "TAN"],
                    ["%{BKY_MATH_TRIG_ASIN}", "ASIN"],
                    ["%{BKY_MATH_TRIG_ACOS}", "ACOS"],
                    ["%{BKY_MATH_TRIG_ATAN}", "ATAN"],
                    ['RAD to DEG', 'RAD2DEG'],
                    ['DEG to RAD', 'DEG2RAD']
                ]
            },
            {
                "type": "input_value",
                "name": "NUM",
                "check": "Number"
            }
        ],
        "inputsInline": true,
        "output": "Number",
        "colour": "%{BKY_MATH_HUE}"
    },
    {
        "type": "math_java_constants",
        "message0": "%1",
        "args0": [
            {
                "type": "field_dropdown",
                "name": "CONSTANT",
                "options": [
                    ["Random [0,1)", "RANDOM"],
                    ["\u03c0", "PI"],
                    ["e", "E"],
                    ["\u221e", "INFINITY"],
                    ["-\u221e", "NINFINITY"],
                    ["NaN", "NAN"]
                ]
            }
        ],
        "output": "Number",
        "colour": "%{BKY_MATH_HUE}"
    },
    {
        "type": "controls_while",
        "message0": "while %1",
        "args0": [
            {
                "type": "input_value",
                "name": "BOOL",
                "check": "Boolean"
            }
        ],
        "message1": "%{BKY_CONTROLS_REPEAT_INPUT_DO} %1",
        "args1": [{
            "type": "input_statement",
            "name": "DO"
        }],
        "previousStatement": null,
        "nextStatement": null,
        "colour": "%{BKY_LOOPS_HUE}"
    },
    {
        "type": "coord_x",
        "message0": "x",
        "output": "Number",
        "colour": "%{BKY_MATH_HUE}"
    },
    {
        "type": "coord_y",
        "message0": "y",
        "output": "Number",
        "colour": "%{BKY_MATH_HUE}"
    },
    {
        "type": "coord_z",
        "message0": "z",
        "output": "Number",
        "colour": "%{BKY_MATH_HUE}"
    },
    {
        "type": "entity_from_deps",
        "message0": "Event/target entity",
        "output": "Entity",
        "colour": "195"
    },
    {
        "type": "source_entity_from_deps",
        "message0": "Source entity",
        "output": "Entity",
        "colour": "195"
    },
    {
        "type": "entity_iterator",
        "message0": "Entity iterator",
        "output": "Entity",
        "colour": "195"
    },
    {
        "type": "immediate_source_entity_from_deps",
        "message0": "Immediate source entity",
        "output": "Entity",
        "colour": "195"
    },
    {
        "type": "math_from_text",
        "message0": "number from text %1",
        "args0": [
            {
                "type": "input_value",
                "name": "NUMTEXT",
                "check": "String"
            }
        ],
        "inputsInline": false,
        "output": "Number",
        "colour": "%{BKY_MATH_HUE}"
    },
    {
        "type": "direction_constant",
        "message0": "",
        "extensions": [
            "direction_list_provider"
        ],
        "output": "Direction",
        "colour": "20"
    },
    {
        "type": "direction_unspecified",
        "message0": "Any direction",
        "output": "Null",
        "colour": "20"
    },
    {
        "type": "direction_from_deps",
        "message0": "Trigger direction/face",
        "output": "Direction",
        "colour": "20"
    },
]);

// add custom loop to loop types
Blockly.Constants.Loops.CONTROL_FLOW_IN_LOOP_CHECK_MIXIN.LOOP_TYPES.push('controls_while');

Blockly.Extensions.register('biome_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("biome"))), 'biome');
    });

Blockly.Extensions.register('biome_dictionary_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("biomedictionary"))), 'biomedict');
    });

Blockly.Extensions.register('entity_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("entity"))), 'entity');
    });

Blockly.Extensions.register('gui_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("gui"))), 'guiname');
    });

Blockly.Extensions.register('rangeditem_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("rangeditem"))), 'rangeditem');
    });

Blockly.Extensions.register('dimension_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("dimension"))), 'dimension');
    });

Blockly.Extensions.register('gamemode_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("gamemode"))), 'gamemode');
    });

Blockly.Extensions.register('damagesource_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("damagesource"))), 'damagesource');
    });

Blockly.Extensions.register('achievement_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("achievement"))), 'achievement');
    });

Blockly.Extensions.register('potion_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("potion"))), 'potion');
    });

Blockly.Extensions.register('gamerulesboolean_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("gamerulesboolean"))), 'gamerulesboolean');
    });

Blockly.Extensions.register('gamerulesnumber_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("gamerulesnumber"))), 'gamerulesnumber');
    });

Blockly.Extensions.register('enhancement_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("enhancement"))), 'enhancement');
    });

Blockly.Extensions.register('sound_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("sound"))), 'sound');
    });

Blockly.Extensions.register('particle_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("particle"))), 'particle');
    });

Blockly.Extensions.register('schematic_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("schematic"))), 'schematic');
    });

Blockly.Extensions.register('fluid_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("fluid"))), 'fluid');
    });

Blockly.Extensions.register('material_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("material"))), 'material');
    });

Blockly.Extensions.register('direction_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("direction"))), 'direction');
    });

Blockly.Extensions.register('dimension_custom_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("dimension_custom"))), 'dimension');
    });
