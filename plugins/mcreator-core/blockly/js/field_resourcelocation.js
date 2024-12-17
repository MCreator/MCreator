class FieldResourceLocation extends Blockly.FieldTextInput {

    doClassValidation_(newValue) {
        if (typeof newValue != 'string') {
            return null;
        }

        if (!/^([a-z0-9_\-\.]+:)?[a-z0-9_\-\.\/]+$/.test(newValue)) {
            return null;
        }

        return newValue;
    }

}

Blockly.fieldRegistry.register('field_resourcelocation', FieldResourceLocation);