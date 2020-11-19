{
    new java.lang.Thread(() -> {
        	try {
        		java.lang.Thread.sleep(java.time.Duration.ofSeconds(${input$SEC}).toMillis());
        	} catch (InterruptedException e) {
        		e.printStackTrace();
        	}
        	${statement$DO}
    }).start();
}