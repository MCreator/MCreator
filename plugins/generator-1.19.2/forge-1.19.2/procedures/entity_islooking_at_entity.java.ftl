(new Object() {
	public boolean isLooking(Entity _source, Entity _target) {
		Vec3 vec3 = _source.getViewVector(1.0F).normalize();
		Vec3 vec31 = new Vec3(_target.getX() - _source.getX(), (_target.getEyeY() - _target.getEyeHeight() * 0.5) - _source.getEyeY(),
				_target.getZ() - _source.getZ());
		double d0 = vec31.length();
		vec31 = vec31.normalize();
		double d1 = vec3.dot(vec31);
		if (_source instanceof LivingEntity _liv)
			return d1 > 1.0D - 0.04D / d0 ? _liv.hasLineOfSight(_target) : false;
		return false;
	}
}.isLooking(${input$entity}, ${input$lookingAt}))