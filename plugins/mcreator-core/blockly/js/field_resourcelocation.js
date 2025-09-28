class FieldResourceLocation extends Blockly.FieldTextInput {
    constructor(text, opt_config) {
        super(text);
        this.allowNamespace_ = opt_config?.allowNamespace ?? true;
    }

    doClassValidation_(newValue) {
        if (typeof newValue != 'string') {
            return null;
        }

        const pathPattern = /^[a-z0-9_\-\.\/]+$/;
        const namespacedPattern = /^([a-z0-9_\-\.]+:)?[a-z0-9_\-\.\/]+$/;
        const pattern = this.allowNamespace_ ? namespacedPattern : pathPattern;

        if (!pattern.test(newValue)) {
            return null;
        }

        return newValue;
    }

    static fromJson(options) {
        return new FieldResourceLocation(options['text'], options);
    }
}

Blockly.fieldRegistry.register('field_resourcelocation', FieldResourceLocation);