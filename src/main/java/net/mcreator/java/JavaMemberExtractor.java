/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.java;

import java.util.HashSet;
import java.util.Set;

public class JavaMemberExtractor {

	/**
	 * Extracts unqualified identifiers referenced in the given code: identifiers that are not reserved
	 * words and are not preceded by a dot. Identifiers reached through qualification (members of other
	 * objects, segments of fully qualified names) resolve through their qualifier and are skipped.
	 * String literals, text blocks, character literals, and comments are ignored.
	 *
	 * @param code The Java code to extract members from
	 * @return Set of members found in the code
	 */
	public static Set<String> getMemberList(String code) {
		Set<String> memberList = new HashSet<>();

		String masked = JavaCodeScanner.maskStringsAndComments(code);

		StringBuilder memberName = new StringBuilder();
		char prevChar = ' ';
		for (int i = 0; i < masked.length(); i++) {
			char c = masked.charAt(i);
			if (memberName.isEmpty()) {
				if (Character.isJavaIdentifierStart(c) && !Character.isJavaIdentifierPart(prevChar) && prevChar != '.')
					memberName.append(c);
			} else if (Character.isJavaIdentifierPart(c)) {
				memberName.append(c);
			} else {
				addMember(memberList, memberName.toString());
				memberName.setLength(0);
			}
			prevChar = c;
		}
		addMember(memberList, memberName.toString());

		return memberList;
	}

	private static void addMember(Set<String> memberList, String member) {
		if (!member.isEmpty() && !JavaConventions.JAVA_RESERVED_WORDS.contains(member))
			memberList.add(member);
	}

}
