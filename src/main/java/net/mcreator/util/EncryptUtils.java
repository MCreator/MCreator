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

package net.mcreator.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class EncryptUtils {

	public static String encrypt(String data) throws Exception {
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.ENCRYPT_MODE, generateKey());
		return new String(Base64.getMimeEncoder().encode(c.doFinal(data.getBytes())));
	}

	public static String decrypt(String encryptedData) throws Exception {
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.DECRYPT_MODE, generateKey());
		return new String(c.doFinal(Base64.getMimeDecoder().decode(encryptedData.getBytes())));
	}

	private static Key generateKey() {
		return new SecretKeySpec(new byte[] { 45, 56, 3, -30, 30, 124, -100, 63, 42, 103, 1, 45, -87, -1, 45, 99 },
				"AES");
	}

}
