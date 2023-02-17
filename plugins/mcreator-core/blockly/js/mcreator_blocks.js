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

Blockly.Blocks['old_command'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.old_command"))
            .appendField(new FieldDataListSelector('procedure'), 'procedure');
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour('%{BKY_TEXTS_HUE}');
    }
};

Blockly.Blocks['call_procedure'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.call_procedure"))
            .appendField(new FieldDataListSelector('procedure'), 'procedure');
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(250);
        this.setMutator(new Blockly.Mutator(['procedure_dependencies_mutator_input'], this));
        this.mixin(PROCEDURE_DEPENDENCIES_MUTATOR_MIXIN);
    }
};

Blockly.Blocks['call_procedure_no_args'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.call_procedure"))
            .appendField(new FieldDataListSelector('procedure'), 'procedure');
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(250);
    }
};

Blockly.Blocks['procedure_dependencies_mutator_container'] = {
    init: function() {
        this.appendDummyInput().appendField(javabridge.t('blockly.block.call_procedure.container'));
        this.appendStatementInput('dependencies');
        this.contextMenu = false;
        this.setColour(250);
    }
};

Blockly.Blocks['procedure_dependencies_mutator_input'] = {
    init: function() {
        this.appendDummyInput().appendField(javabridge.t('blockly.block.call_procedure.input'));
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.contextMenu = false;
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

Blockly.Blocks['args_start'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.cmdargs_start"));
        this.setStyle('hat_blocks');
        this.setNextStatement(true);
        this.setColour(120);
        this.setTooltip(javabridge.t("blockly.block.cmdargs_start.tooltip"));
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

Blockly.Blocks['feature_container'] = {
    init: function () {
        this.appendValueInput('feature').setCheck('Feature').appendField(javabridge.t("blockly.block.feature_container"));
        this.appendDummyInput().appendField(new Blockly.FieldLabel(javabridge.t("blockly.block.feature_container.with_placement")));
        this.setStyle('hat_blocks');
        this.setNextStatement(true, 'Placement');
        this.setColour(340);
        this.setInputsInline(false);
        this.setTooltip(javabridge.t("blockly.block.feature_container.tooltip"));
    }
};

Blockly.Blocks['mcitem_allblocks'] = {
    init: function () {
        let block = this;
        this.appendDummyInput()
            .appendField(new FieldMCItemSelector("allblocks"), "value")
            .appendField(new Blockly.FieldImage("./res/b.png", 8, 36));
        this.setOutput(true, ['MCItemBlock', 'BlockStateProvider']);
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setColour(60);
        this.setTooltip(function () {
            let value = block.getFieldValue('value');
            return value == null ? "Double click to select block" : value;
        });
    }
};

Blockly.Blocks['mcitem_all'] = {
    init: function () {
        let block = this;
        this.appendDummyInput()
            .appendField(new FieldMCItemSelector("all"), "value")
            .appendField(new Blockly.FieldImage("./res/bi.png", 8, 36));
        this.setOutput(true, 'MCItem');
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setColour(350);
        this.setTooltip(function () {
            let value = block.getFieldValue('value');
            return value == null ? "Double click to select item/block" : value;
        });
    }
};

Blockly.Blocks['entity_from_deps'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.entity_from_deps"));
        this.setColour(195);
        this.setOutput(true, 'Entity');
    }
};

Blockly.Blocks['source_entity_from_deps'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.source_entity_from_deps"));
        this.setColour(195);
        this.setOutput(true, 'Entity');
    }
};

Blockly.Blocks['immediate_source_entity_from_deps'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.immediate_source_entity_from_deps"));
        this.setColour(195);
        this.setOutput(true, 'Entity');
    }
};

Blockly.Blocks['entity_iterator'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.entity_iterator"));
        this.setColour(195);
        this.setOutput(true, 'Entity');
    }
};

Blockly.Blocks['entity_none'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(new Blockly.FieldImage('./res/null.png', 8, 24))
            .appendField(javabridge.t("blockly.block.entity_none"));
        this.setColour(195);
        this.setOutput(true, 'Entity');
    }
};

Blockly.Blocks['direction_from_deps'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.direction_from_deps"));
        this.setColour(20);
        this.setOutput(true, 'Direction');
    }
};

Blockly.Blocks['direction_unspecified'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.direction_unspecified"));
        this.setColour(20);
        this.setOutput(true, 'Null');
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

Blockly.Blocks['math_from_text'] = {
    init: function () {
        this.appendValueInput('NUMTEXT').setCheck('String')
            .appendField(javabridge.t("blockly.block.math_from_text"));
        this.setColour('%{BKY_MATH_HUE}');
        this.setOutput(true, 'Number');
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
        this.setColour('%{BKY_LOGIC_HUE}');
    }
};

Blockly.Blocks['text_starts_with'] = {
    init: function () {
        this.appendValueInput('text').setCheck('String')
            .appendField(javabridge.t("blockly.block.text_starts_with.in"));
        this.appendValueInput('starts').setCheck('String')
            .appendField(javabridge.t("blockly.block.text_starts_with.check"));
        this.setInputsInline(true);
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setOutput(true, 'Boolean');
        this.setColour('%{BKY_LOGIC_HUE}');
    }
};

Blockly.Blocks['text_ends_with'] = {
    init: function () {
        this.appendValueInput('text').setCheck('String')
            .appendField(javabridge.t("blockly.block.text_ends_with.in"));
        this.appendValueInput('ends').setCheck('String')
            .appendField(javabridge.t("blockly.block.text_ends_with.check"));
        this.setInputsInline(true);
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setOutput(true, 'Boolean');
        this.setColour('%{BKY_LOGIC_HUE}');
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
        this.setColour('%{BKY_TEXTS_HUE}');
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
        this.setColour('%{BKY_TEXTS_HUE}');
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
        this.setColour('%{BKY_TEXTS_HUE}');
    }
};

Blockly.Blocks['text_is_empty'] = {
    init: function () {
        this.appendValueInput('text').setCheck('String')
            .appendField(javabridge.t("blockly.block.text_is_empty"));
        this.setInputsInline(true);
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setOutput(true, 'Boolean');
        this.setColour('%{BKY_LOGIC_HUE}');
    }
};

Blockly.Blocks['text_trim'] = {
    init: function () {
        this.appendValueInput('text').setCheck('String')
            .appendField(javabridge.t("blockly.block.text_trim"));
        this.setInputsInline(true);
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setOutput(true, 'String');
        this.setColour('%{BKY_TEXTS_HUE}');
    }
};

Blockly.Blocks['text_uppercase'] = {
    init: function () {
        this.appendValueInput('text').setCheck('String')
            .appendField(javabridge.t("blockly.block.text_uppercase"));
        this.setInputsInline(true);
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setOutput(true, 'String');
        this.setColour('%{BKY_TEXTS_HUE}');
    }
};

Blockly.Blocks['text_lowercase'] = {
    init: function () {
        this.appendValueInput('text').setCheck('String')
            .appendField(javabridge.t("blockly.block.text_lowercase"));
        this.setInputsInline(true);
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setOutput(true, 'String');
        this.setColour('%{BKY_TEXTS_HUE}');
    }
};

Blockly.Blocks['time_as_string'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.time_as_string"));
        this.setColour('%{BKY_TEXTS_HUE}');
        this.setOutput(true, 'String');
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
        this.setColour('%{BKY_TEXTS_HUE}');
    }
};

Blockly.Blocks['time_day_of_week'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.time_day_of_week"));
        this.setColour('%{BKY_MATH_HUE}');
        this.setOutput(true, 'Number');
    }
};

Blockly.Blocks['time_day_of_month'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.time_day_of_month"));
        this.setColour('%{BKY_MATH_HUE}');
        this.setOutput(true, 'Number');
    }
};

Blockly.Blocks['time_hours'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.time_hours"));
        this.setColour('%{BKY_MATH_HUE}');
        this.setOutput(true, 'Number');
    }
};

Blockly.Blocks['time_minutes'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.time_minutes"));
        this.setColour('%{BKY_MATH_HUE}');
        this.setOutput(true, 'Number');
    }
};

Blockly.Blocks['time_month'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.time_month"));
        this.setColour('%{BKY_MATH_HUE}');
        this.setOutput(true, 'Number');
    }
};

Blockly.Blocks['time_seconds'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.time_seconds"));
        this.setColour('%{BKY_MATH_HUE}');
        this.setOutput(true, 'Number');
    }
};

Blockly.Blocks['time_year'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.time_year"));
        this.setColour('%{BKY_MATH_HUE}');
        this.setOutput(true, 'Number');
    }
};

Blockly.Blocks['time_week_of_year'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.time_week_of_year"));
        this.setColour('%{BKY_MATH_HUE}');
        this.setOutput(true, 'Number');
    }
};

// Mutator block for "Any/All of" block predicates
Blockly.Blocks['block_predicate_mutator_container'] = {
    init: function() {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.block_predicate_mutator.container"));
        this.appendStatementInput('STACK');
        this.contextMenu = false;
        this.setColour('%{BKY_LOGIC_HUE}');
    }
};

// Mutator block for "Any/All of" block predicates
Blockly.Blocks['block_predicate_mutator_input'] = {
    init: function() {
      this.appendDummyInput().appendField(javabridge.t("blockly.block.block_predicate_mutator.input"));
      this.setPreviousStatement(true);
      this.setNextStatement(true);
      this.contextMenu = false;
      this.setColour('%{BKY_LOGIC_HUE}');
    }
};

// Mutator block for "Block list" mixin
Blockly.Blocks['block_list_mutator_container'] = {
    init: function() {
        this.appendDummyInput().appendField(javabridge.t('blockly.block.block_list_mutator.container'));
        this.appendStatementInput('STACK');
        this.contextMenu = false;
        this.setColour(45);
    }
};

// Mutator block for "Block list" mixin
Blockly.Blocks['block_list_mutator_input'] = {
    init: function() {
      this.appendDummyInput().appendField(javabridge.t('blockly.block.block_list_mutator.input'));
      this.setPreviousStatement(true);
      this.setNextStatement(true);
      this.contextMenu = false;
      this.fieldValues_ = [];
      this.setColour(45);
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
                    ["Random with std. normal distribution", "NORMAL"],
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
        "type": "direction_constant",
        "message0": "",
        "extensions": [
            "direction_list_provider"
        ],
        "output": "Direction",
        "colour": "20"
    }
]);
