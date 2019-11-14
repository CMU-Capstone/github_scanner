package com.scanner.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.json.JsonMode;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * The utility for the project
 * */
public class Util {
	public static MongoClientURI uri = new MongoClientURI(
            "mongodb://github_scanner:github_scanner123!@tbcpipeline-shard-00-00-06hga.mongodb.net:27017,tbcpipeline-shard-00-01-06hga.mongodb.net:27017,tbcpipeline-shard-00-02-06hga.mongodb.net:27017/test?ssl=true&replicaSet=TBCPipeline-shard-0&authSource=admin&retryWrites=true&w=majority");
	public static MongoClient mongoClient;
	
	public static MongoDatabase openConnection() {
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.OFF); 
        
        mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("hackthons");
        return database;
	}
	
	public static void closeConnection() {
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.OFF); 
        
        if (mongoClient != null) {
        	mongoClient.close();
        }
	}
	
	public static String convert(String path) {
		String prefix = "https://raw.githubusercontent.com/";
		String suffix = "/master/README.md";
		String content = path.substring(19);
		return prefix + content + suffix;
	}
	
	public static void mongoSaveRepo(String hackthonName, List<String> repoLinks) {
		MongoDatabase database = openConnection();
		MongoCollection<Document> collection = database.getCollection(hackthonName);
		for (String link : repoLinks) {
			String path = convert(link);
			mongoSaveRepoHelper(path, hackthonName, collection);
		}
		System.out.println("Scan all git repos for " + hackthonName + ", and save to mongoDB successfully");
		closeConnection();
	}
	
	public static void mongoSaveRepoHelper(String path, String hacthonName, MongoCollection<Document> collection) {
		URL url;
		try {
			url = new URL(path);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine;
			Document doc = new Document("hackthon_name", hacthonName);
			while ((inputLine = in.readLine()) != null) {
				System.out.println("everyline: " + inputLine);
				if (inputLine.length() == 0) {
					continue;
				} else if (inputLine.startsWith("# Project name")) {
					String projectName = in.readLine();
					doc.append("project_name", projectName);
				} else if (inputLine.startsWith("# Project problem type")) {
					String problem = in.readLine();
					String[] problems = problem.split(",");
					List<String> problemsList = new ArrayList<>();
					for (String everyProblem : problems) {
						problemsList.add(everyProblem.trim());
					}
					doc.append("problem", problemsList);
				} else if (inputLine.startsWith("# Project industry")) {
					String industry = in.readLine();
					String[] industries = industry.split(",");
					List<String> industriesList = new ArrayList<>();
					for (String everyIndustry : industries) {
						industriesList.add(everyIndustry.trim());
					}
					doc.append("industry", industriesList);
				} else if (inputLine.startsWith("# Technologies used")) {
					String technogloy = in.readLine();
					String[] technologies = technogloy.split(",");
					List<String> technologiesList = new ArrayList<>();
					for (String everyTechnology : technologies) {
						technologiesList.add(everyTechnology.trim());
					}
					doc.append("technology", technologiesList);
				} else if (inputLine.startsWith("# Participants")) {
					List<String> usersList = new ArrayList<>();
					String user = in.readLine();
					System.out.println("user : " + user);
					while (user != null && user.length() != 0) {
						usersList.add(user);
						user = in.readLine();
					}
					List<String> usersListClean = new ArrayList<>();
					for (int i = 0; i < usersList.size(); i++) {
						String email = usersList.get(i).split(":")[1].trim();
						if (i != usersList.size() - 1) {
							email = email.substring(0, email.length() - 5);
						}
						usersListClean.add(email);
					}
					doc.append("email", usersListClean);
				} else if (inputLine.contains("#")){
					System.out.println("!!!!!!!!!!!" + inputLine);
					String otherHeader = inputLine.substring(2, inputLine.length());
					System.out.println("next headers: " + otherHeader);
					StringBuilder sb = new StringBuilder();
					String content = in.readLine();
					while(content != null && content.length() != 0) {
						System.out.println("content: " + content);
						sb.append(content);
						content = in.readLine();
					}
					doc.append(otherHeader, sb.toString());
				} 
			}
			collection.insertOne(doc);
			in.close();
			System.out.println("Add one ducoment: " + doc.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> getAllHackthonNames() {
		List<String> allHackthonNames = new ArrayList<>();
		MongoDatabase database = openConnection();
		for (String everyHackthon: database.listCollectionNames()) {
			allHackthonNames.add(everyHackthon);
		}
		closeConnection();
		Collections.sort(allHackthonNames);
		return allHackthonNames;
	}
	
	public static void resetHackthon(String hackthonName) {
		try {
			MongoDatabase database = openConnection();
			MongoCollection<Document> collection = database.getCollection(hackthonName);
			collection.drop();
			closeConnection();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<Document> search(List<String> hackthon_name, String project_name, String problem, String industry, String technology, String user, String header, String data) {
		List<Document> result = new ArrayList<>();
		List<String> searchRange = new ArrayList<>();
		try {
			MongoDatabase database = openConnection();
			if (hackthon_name == null || hackthon_name.size() == 0) {
				for (String hackthonName : database.listCollectionNames()) {
					searchRange.add(hackthonName);
				}
			} else {
				searchRange = hackthon_name;
			}
			
			MongoCollection<Document> collection;
			for (String hackthon: searchRange) {
				collection = database.getCollection(hackthon);
				
				BasicDBObject andQuery = new BasicDBObject();
				List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
				if (project_name != null &&  project_name.length() != 0) {
					BasicDBObject project_nameOBJ = new BasicDBObject("project_name", project_name);
					obj.add(project_nameOBJ);
				}
				
				if (problem != null && problem.length() != 0) {
					BasicDBObject problemOBJ = new BasicDBObject();
					problemOBJ.put("problem", new BasicDBObject("$regex", problem)
						.append("$options", "si"));
				    obj.add(problemOBJ);;
				}
				
				if (industry != null && industry.length() != 0) {
					BasicDBObject industryOBJ = new BasicDBObject();
					industryOBJ.put("industry", new BasicDBObject("$regex", industry)
						.append("$options", "si"));
				    obj.add(industryOBJ);
				}
				
				if (technology != null && technology.length() != 0) {
					BasicDBObject technologyOBJ = new BasicDBObject();
					technologyOBJ.put("technology", new BasicDBObject("$regex", technology)
						.append("$options", "si"));
				    obj.add(technologyOBJ);
				}
				
				if (user != null && user.length() != 0) {
					BasicDBObject userOBJ = new BasicDBObject("email", user);
					obj.add(userOBJ);
				}
				
				if (header != null && header.length() != 0 && data != null && data.length() != 0) {
					BasicDBObject regexQuery = new BasicDBObject();
					regexQuery.put(header, new BasicDBObject("$regex", data)
						.append("$options", "si"));
				    obj.add(regexQuery);
				}
				
				if (obj != null && obj.size() != 0) {
					andQuery.put("$and", obj);
					System.out.println(andQuery.toString());
				}
				
				MongoCursor<Document> cursor = collection.find(andQuery).iterator();
		        while (cursor!= null && cursor.hasNext()) {
		        	result.add(cursor.next());
		        }
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		closeConnection();
		return result;
	}
}

