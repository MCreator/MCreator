private static double parseDouble(String s) {
	try {
		return Double.parseDouble(s.trim());
	} catch (Exception e) {
		return 0;
	}
}