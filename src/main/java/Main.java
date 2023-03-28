import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String string = listToJson(list);
        writeString(string, "data.json");

        fileName = "data.xml";
        List<Employee> list2 = parseXML(fileName);
        String string2 = listToJson(list2);
        writeString(string2, "data2.json");

        fileName = "new_data.json";
        String json = readString(fileName);
        List<Employee> listFromJsonString = jsonToList(json);
        System.out.println(listFromJsonString);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy columnPositionMappingStrategy = new ColumnPositionMappingStrategy();
            columnPositionMappingStrategy.setType(Employee.class);
            columnPositionMappingStrategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(columnPositionMappingStrategy)
                    .build();
            list = csvToBean.parse();
        } catch (Exception e) {
        }
        return list;
    }

    public static List<Employee> parseXML(String fileName) throws IOException, SAXException, ParserConfigurationException {
        List<Employee> list = new ArrayList<>();
        File file = new File(fileName);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        Node root = document.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                Employee employee = new Employee();
                employee.id = Integer.parseInt(((Element) node)
                        .getElementsByTagName("id")
                        .item(0)
                        .getTextContent());
                employee.firstName = ((Element) node)
                        .getElementsByTagName("firstName")
                        .item(0)
                        .getTextContent();
                employee.lastName = ((Element) node)
                        .getElementsByTagName("lastName")
                        .item(0)
                        .getTextContent();
                employee.country = ((Element) node)
                        .getElementsByTagName("country")
                        .item(0)
                        .getTextContent();
                employee.age = Integer.parseInt(((Element) node)
                        .getElementsByTagName("age")
                        .item(0)
                        .getTextContent());
                list.add(employee);
            }
        }
        return list;
    }

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String jsonString = gson.toJson(list, listType);
        return jsonString;
    }

    public static void writeString(String string, String path) {
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(string);
        } catch (Exception e) {
        }
    }

    public static String readString(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            bufferedReader.lines().forEach(stringBuilder::append);
        } catch (Exception e) {
        }
        return stringBuilder.toString();
    }

    public static List<Employee> jsonToList(String jsonString) {
        List<Employee> list = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) (new JSONParser()).parse(jsonString);
        } catch (ParseException ignored){}
        Gson gson = new GsonBuilder().create();
        for (int i=0; i<jsonArray.size(); i++)
        {
            JSONObject jsonObject= (JSONObject) jsonArray.get(i);
            Employee employee = gson.fromJson(String.valueOf(jsonObject), Employee.class);
            list.add(employee);
        }
        return list;
    }
}
