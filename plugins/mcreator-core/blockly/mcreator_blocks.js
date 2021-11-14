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
            .appendField(new Blockly.FieldDropdown([["DEFAULT", "DEFAULT"], ["ALLOW", "ALLOW"], ["DENY", "DENY"]]), 'result');
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

Blockly.Blocks['java_code'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.java_code"))
            .appendField(new Blockly.FieldMultilineInput("/*code*/"), 'CODE');
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(250);
    }
};

Blockly.Blocks['java_code_get'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.java_code"))
            .appendField(new Blockly.FieldMultilineInput("(null)"), 'CODE');
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

Blockly.Blocks['time_to_formatted_string'] = {
    init: function () {
        this.appendValueInput('format').setCheck('String')
            .appendField(javabridge.t("blockly.block.time_to_formatted_string"));
        this.setInputsInline(true);
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setOutput(true, 'String');
        this.setColour(Blockly.Constants.Text.HUE);
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
                    ["Max", "MAX"],
                    ["hypot", "HYPOT"],
                    ["atan2", "ATAN2"]
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
                    ['cube root', 'CUBEROOT'],
                    ["%{BKY_MATH_SINGLE_OP_ABSOLUTE}", 'ABS'],
                    ['signum', 'SIGNUM'],
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
        "type": "logic_ternary_op",
        "message0": "if %1 then %2 else %3",
        "args0": [
            {
                "type": "input_value",
                "name": "condition",
                "check": "Boolean"
            },
            {
                "type": "input_value",
                "name": "THEN"
            },
            {
                "type": "input_value",
                "name": "ELSE"
            }
        ],
        "inputsInline": true,
        "output": null,
        "colour": "#888888",
        "extensions": [
            "logic_ternary"
        ],
        "mutator": "mark_attached_to_block_item"
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
        "extensions": [
            "is_custom_loop"
        ],
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
    {
        "type": "time_as_string",
        "message0": "Time as text",
        "output": "String",
        "colour": "%{BKY_TEXTS_HUE}"
    },
    {
        "type": "time_day_of_week",
        "message0": "Day of this week",
        "output": "Number",
        "colour": "%{BKY_MATH_HUE}"
    },
    {
        "type": "time_day_of_month",
        "message0": "Day of this month",
        "output": "Number",
        "colour": "%{BKY_MATH_HUE}"
    },
    {
        "type": "time_hours",
        "message0": "Current hour of the day",
        "output": "Number",
        "colour": "%{BKY_MATH_HUE}"
    },
    {
        "type": "time_month",
        "message0": "Current month",
        "output": "Number",
        "colour": "%{BKY_MATH_HUE}"
    },
    {
        "type": "time_minutes",
        "message0": "Minutes past current hour",
        "output": "Number",
        "colour": "%{BKY_MATH_HUE}"
    },
    {
        "type": "time_year",
        "message0": "Current year",
        "output": "Number",
        "colour": "%{BKY_MATH_HUE}"
    },
    {
        "type": "time_seconds",
        "message0": "Seconds past current minute",
        "output": "Number",
        "colour": "%{BKY_MATH_HUE}"
    },
    {
        "type": "time_week_of_year",
        "message0": "Week of the current year",
        "output": "Number",
        "colour": "%{BKY_MATH_HUE}"
    }
]);
