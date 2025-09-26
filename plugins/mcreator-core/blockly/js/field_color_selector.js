/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

class FieldColorSelector extends Blockly.Field {

    EDITABLE = true;
    SERIALIZABLE = true;
    CURSOR = 'default';

    constructor(color = '#ffffff', opt_validator, opt_config) {
        super(color, opt_validator, opt_config);
        this.color_ = color || '#ffffff';

        if (opt_config) {
            this.configure_(opt_config);
        }

        this.size_ = new Blockly.utils.Size(40, 20);
        this.setTooltip(() => (this.color_ || '').toUpperCase());
    };

    static fromJson(options) {
        return new this(options['color'] || '#ffffff', undefined, options);
    };

    onMouseDown_(e) {
        if (this.sourceBlock_ && !this.sourceBlock_.isInFlyout) {
            e.stopPropagation();
            let thisField = this;
            javabridge.openColorSelector(this.color_, {
                'callback': function (value) {
                    const group = Blockly.Events.getGroup();
                    Blockly.Events.setGroup(true);
                    thisField.setValue(value);
                    Blockly.Events.setGroup(group);
                    javabridge.triggerEvent();
                }
            });
        }
    };

    doValueUpdate_(newValue) {
        this.color_ = newValue;
        super.doValueUpdate_(newValue);
        this.render_(); // force rerender after value change
    };

    initView() {
        const g = Blockly.utils.dom.createSvgElement('g', {}, this.fieldGroup_);
        this.rect_ = Blockly.utils.dom.createSvgElement('rect', {
            rx: 2,
            ry: 2,
            height: this.size_.height,
            width: this.size_.width,
            stroke: '#ccc',
            'stroke-width': 2,
            fill: this.color_
        }, g);
    }

    render_() {
        if (this.rect_) {
            this.rect_.setAttribute('fill', this.color_ || '#ffffff');
        }
    }

    getText_() {
        return this.color_;
    }
}

Blockly.fieldRegistry.register('field_color_selector', FieldColorSelector);