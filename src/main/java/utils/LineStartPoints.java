package utils;

import model.MapPoint;
import model.Pair;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;


public class LineStartPoints {
	Map<Pair<Integer, Integer>, MapPoint> parseMap = new HashMap<>();
	Map<String, MapPoint> subwayParse = new HashMap<>();
	Map<String, MapPoint> trainParse = new HashMap<>();

	public LineStartPoints() throws IOException {
		createParse();
	}

	public MapPoint parseRoute(int routeID, int directionID){
		return parseMap.get(new Pair<>(routeID, directionID));
	}
	public MapPoint parseSubway(String line){
		return subwayParse.get(line);
	}
	public MapPoint parseTrains(String line){
		return trainParse.get(line);
	}

	private void createParse() throws IOException {
		String fileName = "/recorrido-colectivos.csv";
		InputStream is = LineStartPoints.class.getResourceAsStream(fileName);
		Reader in = new InputStreamReader(is);
		Iterable<CSVRecord> records = CSVFormat.DEFAULT
				.withFirstRecordAsHeader().parse(in);
		String lng = "", lat;
		for (CSVRecord record : records) {
			String value = record.get("WKT");
			for (int i = 12, match = 0; i < value.length() && match==0; i++){
				if (value.charAt(i) == ' ') {
					lng = value.substring(12,i);
				}else if (value.charAt(i) == ',' || i == value.length() - 1) {
					lat=value.substring(12 + lng.length(),i);
					MapPoint coord = new MapPoint(Double.parseDouble(lat),Double.parseDouble(lng));
					Pair<Integer, Integer> key = new Pair<>(Integer.parseInt(record.get("route_id")), Integer.parseInt(record.get("direction_id")));
					parseMap.put(key,coord);
					match = 1;
				}
			}
		}
		in.close();

		String fileNameSub = "/terminales-de-subte.csv";
		InputStream su = LineStartPoints.class.getResourceAsStream(fileNameSub);
		Reader reader = new InputStreamReader(su);
		Iterable<CSVRecord> records1 = CSVFormat.DEFAULT
				.withFirstRecordAsHeader().parse(reader);
		for (CSVRecord subway : records1){
			String line = subway.get("linea");
			subwayParse.putIfAbsent(line,
					new MapPoint(Double.parseDouble(subway.get("lat")),Double.parseDouble(subway.get("long"))));
		}
		reader.close();

		String fileNameTr = "/terminales-de-trenes-capital.csv";
		InputStream tr = LineStartPoints.class.getResourceAsStream(fileNameTr);
		Reader reader1 = new InputStreamReader(tr);
		Iterable<CSVRecord> records2 = CSVFormat.DEFAULT
				.withFirstRecordAsHeader().parse(reader1);
		for (CSVRecord record: records2){
			String train = record.get("ramal");
			trainParse.putIfAbsent(train,
					new MapPoint(Double.parseDouble(record.get("lat")),Double.parseDouble(record.get("long"))));
		}
	}
}