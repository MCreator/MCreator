{
	List<? extends Entity> _passengers = new ArrayList<>(${input$entity}.getPassengers());
	for (Entity entityiterator : _passengers) {
		${statement$foreach}
	}
}