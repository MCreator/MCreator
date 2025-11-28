private static double doubleFromText(String s) {
	try {
		return Double.parseDouble(s.trim());
		} catch (Exception e) {}
	return 0;
}