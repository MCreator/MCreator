(new Object() {
	public DyeColor randomDyeColor() {
		DyeColor[] colors = DyeColor.values();
		return colors[(int) (Math.random() * 16)];
	}
}.randomDyeColor())