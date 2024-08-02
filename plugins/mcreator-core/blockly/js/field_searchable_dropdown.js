/**
 * This class represent a Searchable dropdown field
 */
class FieldSearchableDropdown extends Blockly.FieldDropdown {
    constructor(options, validator) {
        super(options, validator);

        this.fullOptions_ = options;
        this.searchText = '';

        this.searchLabel_ = document.createElement('div');
        this.searchLabel_.classList.add('blocklySearchableDropDownLabel');
    }

    showEditor_() {
        super.showEditor_();

        const contentDiv = Blockly.DropDownDiv.getContentDiv();

        contentDiv.insertBefore(this.searchLabel_, contentDiv.firstChild);

        this.searchText = '';
        this.searchLabel_.textContent = '';
        this.updateOptions_();

        document.addEventListener('keydown', this.onKeyDown_.bind(this), true);
    }

    hide_() {
        super.hide_();

        this.searchText = '';
        this.searchLabel_.textContent = '';

        document.removeEventListener('keydown', this.onKeyDown_.bind(this), true);
    }

    onKeyDown_(e) {
        e.stopImmediatePropagation(); // To prevent the block from being deleted with Backspace
        e.preventDefault();

        let key = this.getKeyFromEvent_(e);

        if (key === 'Backspace' && e.ctrlKey && this.searchText.length >= 1) {
        	// Handle CTRL + Backspace
            this.searchText = this.searchText.split(' ').slice(0, -1).join(' ') + ' ';
        } else if (key === 'Backspace' && this.searchText.length >= 1) {
        	// Handle Backspace
        	this.searchText = this.searchText.slice(0, -1);
        } else if (key === 'Space') {
        	// Handle Space
            this.searchText += ' ';
        } else if (typeof key === 'string' && key.length === 1  && /^[a-zA-Z0-9]+$/.test(key)) {
            this.searchText += key;
        }

        if (this.searchText === ' ') {
        	this.searchText = '';
        }

        this.updateOptions_();
    }

    getKeyFromEvent_(e) {
        switch (e.keyCode) {
            case 8:
                return 'Backspace';
            case 32:
                return 'Space';
            default:
                return String.fromCharCode(e.keyCode);
        }
    }

    updateOptions_() {
        const menu = this.menu_;

        if (menu) {
        	const menuElement = menu.getElement();

        	menu.menuItems.length = 0;
        	while (menuElement.firstChild) {
        	    menuElement.removeChild(menuElement.firstChild);
        	}

        	this.fullOptions_.forEach(option => {
        	    if (option[0].toLowerCase().includes(this.searchText.toLowerCase())) {
        	        const menuItem = new Blockly.MenuItem(option[0]);
        	        menuItem.setCheckable(true);
        	        menuItem.setChecked(option[1] === this.value_);

        	        menu.addChild(menuItem);

        	        const menuItemElement = menuItem.createDom();

        	        menuElement.appendChild(menuItemElement);
        	        menuItemElement.addEventListener('click', () => {
        	            this.setValue(option[1]);
        	            Blockly.DropDownDiv.hideIfOwner(this);
        	        });

        	        if (option[1] === this.value_) {
        	        	menuItem.setHighlighted(true);
        	        	menuItemElement.scrollIntoView({ 'behavior': 'smooth', 'block': 'nearest' });
        	        }
        	    }
        	});

        	if (this.searchText) {
        		this.searchLabel_.textContent = this.searchText.toLowerCase();
        		this.searchLabel_.style.display = 'inline-block';
        	} else {
        		this.searchLabel_.style.display = 'none'; // Hide searchLabel if searchText is empty
        	}
        }
    }
}

Blockly.fieldRegistry.register('field_searchable_dropdown', FieldSearchableDropdown);