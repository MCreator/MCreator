// Helper function to get the min and max values of a given int provider as an array of [min, max]
function getIntProviderMinMax(providerBlock) {
    // If the int provider block is missing, return undefined
    if (!providerBlock)
        return undefined;

    // Check the value of the constant int provider
    if (providerBlock.type === 'int_provider_constant') {
        let blockValue = providerBlock.getField('value').getValue();
        return [blockValue, blockValue];
    }
    // Check the values for the weighted list int provider
    else if (providerBlock.type === 'int_provider_weighted') {
        // Weighted lists always have at least one input, so the actual returned value won't be [Infinity, -Infinity]
        let retval = [Infinity, -Infinity];
        for (let i = 0, input; input = providerBlock.inputList[i]; i++) {
            if (!input.connection) {
                continue;
            }
            const targetBlockMinMax = getIntProviderMinMax(input.connection.targetBlock());
            // One of the inputs is missing or not properly defined, return undefined
            if (!targetBlockMinMax)
                return undefined;
            // Compare the min values
            if (targetBlockMinMax[0] < retval[0])
                retval[0] = targetBlockMinMax[0];
            // Compare the max values
            if (targetBlockMinMax[1] > retval[1])
                retval[1] = targetBlockMinMax[1]
        }
        return retval;
    }
    // Check the values for the other "terminal" int providers
    else if (providerBlock.type !== 'int_provider_clamped') {
        let blockMin = providerBlock.getField('min').getValue();
        let blockMax = providerBlock.getField('max').getValue();
        return [blockMin, blockMax];
    }
    // Check the values for the clamped int provider
    else {
        let blockMin = providerBlock.getField('min').getValue();
        let blockMax = providerBlock.getField('max').getValue();
        let clampedBlockMinMax = getIntProviderMinMax(providerBlock.getInput('toClamp').connection.targetBlock());
        // If the clamped block input is missing, return undefined
        if (!clampedBlockMinMax)
            return undefined;
        // Otherwise, compare the endpoints of the int provider ranges, then clamp them accordingly
        else
            return [Math.min(Math.max(blockMin, clampedBlockMinMax[0]), blockMax),
                Math.max(Math.min(blockMax, clampedBlockMinMax[1]), blockMin)];
    }
}

// Helper function to check if the value of a given int provider is within a certain range
function isIntProviderWithinBounds(providerBlock, min, max) {
    let intProviderMinMax = getIntProviderMinMax(providerBlock);
    // If the int provider block is missing or doesn't have all the inputs, don't perform any validation
    if (!intProviderMinMax)
        return true;

    return intProviderMinMax[0] >= min && intProviderMinMax[1] <= max;
}

// Helper function for extensions that validate one or more int provider inputs
// The inputs to check and their bounds are passed as arrays of [inputName, min, max]
// The localization key of warnings is "blockly.extension.block_type.input_name"
function validateIntProviderInputs(...inputs) {
    return function () {
        this.setOnChange(function (changeEvent) {
            // Trigger the change only if a block is changed, moved, deleted or created
            if (changeEvent.type !== Blockly.Events.BLOCK_CHANGE &&
                changeEvent.type !== Blockly.Events.BLOCK_MOVE &&
                changeEvent.type !== Blockly.Events.BLOCK_DELETE &&
                changeEvent.type !== Blockly.Events.BLOCK_CREATE) {
                return;
            }
            var isValid = true;
            // For each passed input, we check if it's within bounds
            for (var i = 0; i < inputs.length; i++) {
                var countValue = this.getInput(inputs[i][0]).connection.targetBlock();
                isValid = isIntProviderWithinBounds(countValue, inputs[i][1], inputs[i][2]);
                if (!isValid)
                    break; // Stop checking as soon as one input isn't valid
            }
            if (!this.isInFlyout) {
                // Add a warning for the first non-valid input
                this.setWarningText(isValid ? null : javabridge.t('blockly.extension.' + this.type + '.' + inputs[i][0]));
                const group = Blockly.Events.getGroup();
                // Makes it so the block change and the disable event get undone together.
                Blockly.Events.setGroup(changeEvent.group);
                this.setEnabled(isValid);
                Blockly.Events.setGroup(group);
            }
        });
    };
}

Blockly.Extensions.register('count_placement_validator', validateIntProviderInputs(['count', 0, 256]));

Blockly.Extensions.register('offset_placement_validator', validateIntProviderInputs(['xz', -16, 16], ['y', -16, 16]));

Blockly.Extensions.register('delta_feature_validator', validateIntProviderInputs(['size', 0, 16], ['rimSize', 0, 16]));

Blockly.Extensions.register('replace_sphere_validator', validateIntProviderInputs(['radius', 0, 12]));

Blockly.Extensions.register('simple_column_validator', validateIntProviderInputs(['height', 0, Infinity]));

Blockly.Extensions.register('state_provider_int_property_validator', validateIntProviderInputs(['value', 0, Infinity]));

Blockly.Extensions.register('pine_tree_feature_validator', validateIntProviderInputs(['foliage_height', 0, 24]));

Blockly.Extensions.register('spruce_tree_feature_validator', validateIntProviderInputs(['radius', 0, 16], ['trunk_height', 0, 24]));

Blockly.Extensions.register('azalea_tree_feature_validator', validateIntProviderInputs(['bend_length', 1, 64]));

Blockly.Extensions.register('cherry_tree_feature_validator', validateIntProviderInputs(['branch_count', 1, 3], ['branch_length', 2, 16]));
