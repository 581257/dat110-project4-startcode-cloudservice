package no.hvl.dat110.ac.restservice;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;
import static spark.Spark.post;
import static spark.Spark.delete;

import com.google.gson.Gson;

/**
 * Hello world!
 *
 */
public class App {

	static AccessLog accesslog = null;
	static AccessCode accesscode = null;

	public static void main(String[] args) {

		if (args.length > 0) {
			port(Integer.parseInt(args[0]));
		} else {
			port(8080);
		}

		// objects for data stored in the service

		accesslog = new AccessLog();
		accesscode = new AccessCode();

		after((req, res) -> {
			res.type("application/json");
		});

		// for basic testing purposes
		get("/accessdevice/hello", (req, res) -> {

			Gson gson = new Gson();

			return gson.toJson("IoT Access Control Device");
		});

		// TODO: implement the routes required for the access control service
		// as per the HTTP/REST operations describined in the project description

		// should record an access attempt by storing the log-message contained in the
		// body of the HTTP request in the cloud service
		post("/accessdevice/log", (reg, res) -> {
			Gson gson = new Gson();
			AccessMessage message = gson.fromJson(reg.body(), AccessMessage.class);
			int id = accesslog.add(message.getMessage());
			return id;
		});

		// should return a JSON-representation of all access log entries in the system,
		// i.e., collection of objects of class AccessEntry.java
		get("/accessdevice/log", (reg, res) -> {
			return accesslog.toJson();
		});

		// should return a JSON representation of the access entry identified by {id}
		get("/accessdevice/log/:id", (reg, res) -> {
			Gson gson = new Gson();
			int id = Integer.parseInt(reg.params("id"));

			return gson.toJson(accesslog.get(id));
		});

		// should update the access code stored in the cloud service to a combination of
		// the 1 and 2 buttons. The new access code is to be contained in JSON format in
		// the body of the request as a representation an an object of the class
		// AccessCode.java
		put("/accessdevice/code", (reg, res) -> {
			Gson gson = new Gson();
			AccessCode code = gson.fromJson(reg.body(), AccessCode.class);

			accesscode.setAccesscode(code.getAccesscode());

			return gson.toJson(code);
		});

		// should return a JSON-representation of the current access code stored in the
		// server. This is what the access device will used in order to update the
		// access code.
		get("/accessdevice/code", (reg, res) -> {
			Gson gson = new Gson();
			return gson.toJson(accesscode);

		});

		// should delete all entries in the access log and a return a
		// JSON-representation of the empty access log in the body of the HTTP response.

		delete("/accessdevice/log", (reg, res) -> {
			Gson gson = new Gson();
			accesslog.clear();
			accesslog = new AccessLog();
			return gson.toJson(accesslog);

		});
	}

}
