{
	List<? extends Entity> _passengers = new ArrayList<>(${input$entity}.getRecursivePassengers());
	for (Entity entityiterator : _passengers) {
		${statement$foreach}
	}
}