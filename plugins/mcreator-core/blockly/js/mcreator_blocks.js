// Helper function to register "container" blocks that appear inside simple input mutators
function registerSimpleMutatorContainer(blockId, localizationKey, colour) {
    Blockly.Blocks[blockId] = {
        init: function () {
            this.appendDummyInput().appendField(javabridge.t(localizationKey));
            this.appendStatementInput('STACK');
            this.setColour(colour);
        }
    };
}

// Helper function to register "input" blocks that appear inside simple input mutators
function registerSimpleMutatorInput(blockId, localizationKey, colour, hasFields) {
    Blockly.Blocks[blockId] = {
        init: function () {
            this.appendDummyInput().appendField(javabridge.t(localizationKey));
            this.setPreviousStatement(true);
            this.setNextStatement(true);
            this.setColour(colour);
            if (hasFields)
                this.fieldValues_ = [];
        }
    };
}

Blockly.Blocks['event_trigger'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.event_trigger.line1"));
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.event_trigger.line2"))
            .appendField(new FieldDataListSelector('global_triggers'), 'trigger');
        this.setNextStatement(true);
        this.setStyle('hat_blocks');
        this.setColour(90);
        this.setTooltip(javabridge.t("blockly.block.event_trigger.tooltip"));
    }
};

Blockly.Blocks['script_trigger'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.script_trigger"))
            .appendField(new FieldDataListSelector('global_triggers'), 'trigger');
        this.setNextStatement(true);
        this.setStyle('hat_blocks');
        this.setColour(90);
        this.setTooltip(javabridge.t("blockly.block.script_trigger.tooltip"));
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
        this.setStyle('hat_blocks');
        this.setNextStatement(true);
        this.setColour(150);
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

Blockly.Blocks['enchantment_effects_start'] = {
    init: function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.enchantment_effects_start"));
        this.setStyle('hat_blocks');
        this.setNextStatement(true, 'EnchantmentComponent');
        this.setColour(150);
    }
};

// Blocks that can't be moved to JSON / internal blocks system fully (yet)

Blockly.Blocks['controls_while'] = {
    init: function () {
        this.appendValueInput('BOOL').setCheck('Boolean')
            .appendField(javabridge.t("blockly.block.controls_while"));
        this.appendStatementInput('DO')
            .appendField('%{BKY_CONTROLS_REPEAT_INPUT_DO}');
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour('%{BKY_LOOPS_HUE}');
        Blockly.Extensions.apply('is_custom_loop', this, false);
    }
};

// Mutator blocks for procedure dependencies
registerSimpleMutatorContainer('procedure_dependencies_mutator_container', 'blockly.block.call_procedure.container', 250);
registerSimpleMutatorInput('procedure_dependencies_mutator_input', 'blockly.block.call_procedure.input', 250, true);

// Mutator blocks for "Any/All of" block predicates
registerSimpleMutatorContainer(
    'block_predicate_mutator_container', 'blockly.block.block_predicate_mutator.container', '%{BKY_LOGIC_HUE}');
registerSimpleMutatorInput(
    'block_predicate_mutator_input', 'blockly.block.block_predicate_mutator.input', '%{BKY_LOGIC_HUE}');

// Mutator blocks for "Block list" mixin
registerSimpleMutatorContainer('block_list_mutator_container', 'blockly.block.block_list_mutator.container', 45);
registerSimpleMutatorInput('block_list_mutator_input', 'blockly.block.block_list_mutator.input', 45, true);

// Mutator blocks for geode feature mixin
registerSimpleMutatorContainer('geode_crystal_mutator_container', 'blockly.block.geode_crystal_mutator.container', 0);
registerSimpleMutatorInput('geode_crystal_mutator_input', 'blockly.block.geode_crystal_mutator.input', 0);

// Mutator blocks for ore features mixin
registerSimpleMutatorContainer('ore_mutator_container', 'blockly.block.ore_mutator.container', 0);
registerSimpleMutatorInput('ore_mutator_input', 'blockly.block.ore_mutator.input', 0);

// Mutator blocks for "Weighted list" mixins
registerSimpleMutatorContainer(
    'weighted_list_mutator_container', 'blockly.block.weighted_list_mutator.container', '#888888');
registerSimpleMutatorInput('weighted_list_mutator_input', 'blockly.block.weighted_list_mutator.input', '#888888', true);

// Mutator blocks for "Simple random feature selector" feature mixin
registerSimpleMutatorContainer(
    'feature_simple_random_mutator_container', 'blockly.block.feature_simple_random_mutator.container', 340);
registerSimpleMutatorInput(
    'feature_simple_random_mutator_input', 'blockly.block.feature_simple_random_mutator.input', 340);

// Mutator blocks for tree decorator mixin
registerSimpleMutatorContainer(
    'tree_decorator_mutator_container', 'blockly.block.tree_decorator_mutator.container', 320);
registerSimpleMutatorInput('tree_decorator_mutator_input', 'blockly.block.tree_decorator_mutator.input', 320);

// Mutator blocks for "Direction list" mixin
registerSimpleMutatorContainer(
    'direction_list_mutator_container', 'blockly.block.direction_list_mutator.container', 30);
registerSimpleMutatorInput('direction_list_mutator_input', 'blockly.block.direction_list_mutator.input', 30, true);

// Mutator blocks for "Blockstate selector" mixin
registerSimpleMutatorContainer(
    'blockstate_selector_mutator_container', 'blockly.block.blockstate_selector_mutator.container', 60);
registerSimpleMutatorInput(
    'blockstate_selector_mutator_input', 'blockly.block.blockstate_selector_mutator.input', 60, true);

// Mutator blocks for column feature mixin
registerSimpleMutatorContainer(
    'feature_block_column_mutator_container', 'blockly.block.feature_block_column_mutator.container', '#888888');
registerSimpleMutatorInput(
    'feature_block_column_mutator_input', 'blockly.block.feature_block_column_mutator.input', '#888888');

// Mutator blocks for disk feature mixin
registerSimpleMutatorContainer(
    'feature_disk_mutator_container', 'blockly.block.feature_disk_mutator.container', '#888888');
registerSimpleMutatorInput('feature_disk_mutator_input', 'blockly.block.feature_disk_mutator.input', '#888888');

// Mutator blocks for fixed placement mixin
registerSimpleMutatorContainer(
    'fixed_placement_mutator_container', 'blockly.block.placement_fixed_mutator.container', 130);
registerSimpleMutatorInput(
    'fixed_placement_mutator_input', 'blockly.block.placement_fixed_mutator.input', 130, true);

// Mutator blocks for effect entry advancement trigger mixin
registerSimpleMutatorContainer(
    'player_effect_changed_mutator_container', 'blockly.block.player_effect_changed_mutator.container', 250);
registerSimpleMutatorInput('player_effect_changed_mutator_input', 'blockly.block.player_effect_changed_mutator.input', 250);

// Mutator blocks for enchantment entry advancement trigger mixin
registerSimpleMutatorContainer('item_enchanted_mutator_container', 'blockly.block.item_enchanted_mutator.container', 290);
registerSimpleMutatorInput('item_enchanted_mutator_input', 'blockly.block.item_enchanted_mutator.input', 290);

// Mutator blocks for item condition component advancement trigger mixin
registerSimpleMutatorContainer('item_predicate_mutator_container', 'blockly.block.item_predicate_mutator.container', 90);
registerSimpleMutatorInput('item_predicate_mutator_input', 'blockly.block.item_predicate_mutator.input', 90);

// Mutator blocks for enchantment entry advancement trigger mixin
registerSimpleMutatorContainer('any_item_mutator_container', 'blockly.block.any_item_mutator.container', 350);
registerSimpleMutatorInput('any_item_mutator_input', 'blockly.block.any_item_mutator.input', 350);

// Unregister blocks that we will register again later
delete Blockly.Blocks['controls_flow_statements'];
delete Blockly.Blocks['text_replace'];
delete Blockly.Blocks['text_trim'];

// Register multiline input for JSON use
Blockly.fieldRegistry.register('field_multilinetext', FieldMultilineInput);