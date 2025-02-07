/*
 * Minequery
 * Copyright (C) 2011 Vex Software LLC
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minestatus.minequery.net;

import br.com.brunoxkk0.Hook;
import net.minestatus.minequery.Minequery;
import net.minestatus.minequery.util.helper.DataHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Handles Minequery requests.
 * 
 * @author Kramer Campbell
 * @author Blake Beaupain
 * @since 1.2
 */
public final class RequestHandler extends Thread {
	/**
	 * The socket we are using to obtain a request.
	 */
	private final Socket socket;
	
	/**
	 * The socket manager
	 */
	private final QueryServer server;
	
	/**
	 * Creates a new <code>QueryServer</code> object.
	 * 
	 * @param socket
	 *            The socket we are using to obtain a request
	 */
	public RequestHandler(Socket socket, QueryServer server) {
		this.socket = socket;
		this.server = server;
	}

	/**
	 * Listens for a request.
	 */
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// Read the request and handle it.
			handleRequest(socket, reader.readLine());
			
			// Release the address
			server.releaseSocket(socket);
			
			// Finally close the socket.
			socket.close();
		} catch (IOException ignored) {
		} catch (Exception ex) {
			Minequery.getInstance().log(Level.SEVERE, "Uh oh! Something unexpected happened.", ex);
		}
	}

	/**
	 * Handles a received request.
	 * 
	 * @param socket
	 * 			  The client we are handling the request for.
	 * @param request
	 *            The request message.
	 * @throws java.io.IOException
	 *            If an I/O error occurs.
	 * @throws org.json.JSONException
	 * 			  If a JSON error occurs.
	 */
	private void handleRequest(Socket socket, String request) throws IOException, JSONException {
		if (request == null) {
			return;
		}

		/*
		brunoxkk0 code start

		Implements a modular handler request...
		 */
		if(Hook.canHandle(request)){
			//load data
			String data = Hook.handle(request);

			// Send the response.
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes(data);
		}

		/*
		brunoxkk0 code finish
		 */


		// Handle a standard Minequery request.
		if (request.equalsIgnoreCase("QUERY")) {
			Map<String, Object> data = DataHelper.getData();

			// Build the response.
			StringBuilder resp = new StringBuilder();
			resp.append("SERVERPORT ").append(data.get("serverPort")).append("\n");
			resp.append("PLAYERCOUNT ").append(data.get("playerCount")).append("\n");
			resp.append("MAXPLAYERS ").append(data.get("maxPlayers")).append("\n");
			resp.append("PLAYERLIST ").append(new JSONArray(data.get("playerList")).toString()).append("\n");
			resp.append("SERVERNAME ").append(data.get("serverName")).append("\n");
			resp.append("SERVERIP ").append(data.get("serverIP")).append("\n");
			resp.append("EXTENDEDPLAYERLIST ").append(new JSONArray((List<?>) data.get("extendedPlayerList")).toString()).append("\n");
			resp.append("PLUGINS ").append(new JSONArray((List<?>) data.get("plugins")).toString()).append("\n");
			resp.append("VERSIONS ").append(new JSONObject((Map<?, ?>) data.get("versions")).toString()).append("\n");

			// Send the response.
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes(resp.toString());
		}

		// Handle a request, respond in JSON format.
		if (request.equalsIgnoreCase("QUERY_JSON")) {
			// Build the JSON response.
			StringBuilder resp = new StringBuilder();
			resp.append(new JSONObject(DataHelper.getData()).toString()).append("\n");

			// Send the JSON response.
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes(resp.toString());
		}

		// Responses simply with the version of Minequery.
		if (request.equalsIgnoreCase("VERSION")) {
			// Build the response.
			StringBuilder resp = new StringBuilder();
			resp.append(Minequery.getInstance().getDescription().getVersion()).append("\n");

			// Send the response.
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes(resp.toString());
		}

		// Different requests may be introduced in the future.
	}
}
