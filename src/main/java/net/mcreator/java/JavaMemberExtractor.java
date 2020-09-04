/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

class JavaMemberExtractor {

	enum ParseState {
		IDLE, INSIDE_INLINE_COMMENT, INSIDE_BLOCK_COMMENT, INSIDE_MEMBERNAME, INSIDE_STRING, INSIDE_STRING_ESCAPE_SEQENCE
	}

	static Set<String> getMemberList(String code) {
		Set<String> memberList = new HashSet<>();

		ParseState currentState = ParseState.IDLE;
		ParseState prevState = ParseState.IDLE;

		StringBuilder memberName = new StringBuilder();

		int backShlashesCounter = 0;
		char prevChar = ' ';
		for (int i = 0; i < code.length(); i++) {
			char c = code.charAt(i);
			switch (currentState) {
			case IDLE:
			case INSIDE_MEMBERNAME:
				if (c == '/' && prevChar == '/')
					currentState = ParseState.INSIDE_INLINE_COMMENT;
				else if (c == '*' && prevChar == '/')
					currentState = ParseState.INSIDE_BLOCK_COMMENT;
				else if (c == '"')
					currentState = ParseState.INSIDE_STRING;
				else if (currentState == ParseState.IDLE && Character.isJavaIdentifierStart(c) && !Character
						.isJavaIdentifierPart(prevChar) && prevChar != '.') {
					currentState = ParseState.INSIDE_MEMBERNAME;
					memberName.append(c);
				} else if (currentState == ParseState.INSIDE_MEMBERNAME && Character.isJavaIdentifierPart(c))
					memberName.append(c);
				else
					currentState = ParseState.IDLE;
				break;
			case INSIDE_INLINE_COMMENT:
				if (c == '\n' || c == '\r')
					currentState = ParseState.IDLE;
				break;
			case INSIDE_STRING_ESCAPE_SEQENCE:
				if (c == '\\') { // more escape characters, could be \\\
					backShlashesCounter++;
					break;
				} else {
					currentState = ParseState.INSIDE_STRING; // we are back in string, don't break switch as we need to parse string below
				}
			case INSIDE_STRING:
				if (c == '\\') {
					currentState = ParseState.INSIDE_STRING_ESCAPE_SEQENCE;
					backShlashesCounter = 0; // this back slash is not counted in
				} else if (c == '"' && (prevChar != '\\' || backShlashesCounter % 2 != 0)) {
					// " is end of string, except if there is \ before (escaped double quote)
					// or if before last \ was even number of \\, so escape is for previous \, not for "
					currentState = ParseState.IDLE;
				}
				break;
			case INSIDE_BLOCK_COMMENT:
				if (c == '/' && prevChar == '*')
					currentState = ParseState.IDLE;
				break;
			}

			if (prevState == ParseState.INSIDE_MEMBERNAME && currentState != ParseState.INSIDE_MEMBERNAME) {
				String member = memberName.toString();
				if (!JavaConventions.JAVA_RESERVED_WORDS.contains(member))
					memberList.add(member);
				memberName.setLength(0);
			}

			prevChar = c;
			prevState = currentState;
		}

		return memberList;
	}

}
