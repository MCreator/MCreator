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

package net.mcreator.io;

import com.google.gson.Gson;
import net.mcreator.util.EncryptUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PasswordVault {

	private static final Logger LOG = LogManager.getLogger("Vault");

	private final Map<UUID, String> vault = new HashMap<>();

	public static final PasswordVault INSTANCE = load();

	public String getPassword(UUID passid) {
		return vault.get(passid);
	}

	public void addPassword(UUID passid, String password) {
		vault.putIfAbsent(passid, password);

		try {
			FileIO.writeStringToFile(EncryptUtils.encrypt(new Gson().toJson(this)),
					UserFolderManager.getFileFromUserFolder("vault"));
		} catch (Exception e1) {
			LOG.error(e1.getMessage(), e1);
		}
	}

	private static PasswordVault load() {
		if (UserFolderManager.getFileFromUserFolder("vault").isFile()) {
			String data = FileIO.readFileToString(UserFolderManager.getFileFromUserFolder("vault"));
			try {
				String vaultplain = EncryptUtils.decrypt(data);
				return new Gson().fromJson(vaultplain, PasswordVault.class);
			} catch (Exception e) {
				LOG.warn("Vault load error", e);
			}
		}

		return new PasswordVault();
	}

}
