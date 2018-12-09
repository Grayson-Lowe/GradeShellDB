package edu.boisestate.cs410.gradebook;

import com.budhash.cliche.Command;
import com.budhash.cliche.ShellFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;


public class GradeBookShell {

    private final Connection db;
    private int activeClass = 0;

    public GradeBookShell(Connection cxn) {
        db = cxn;
    }

    public static void main(String[] args) throws IOException, SQLException {
        String dbUrl = args[0];
        try (Connection cxn = DriverManager.getConnection("jdbc:" + dbUrl)) {
            GradeBookShell shell = new GradeBookShell(cxn);
            ShellFactory.createConsoleShell("grades","",shell)
                    .commandLoop();
        }
    }

    @Command(name="new-class")
    public void newClass (String course, String term,int year, int section, String description) throws SQLException
    {
        String query =
                "INSERT INTO class(course_number,class_year,class_term,class_section,class_description) " +
                        "VALUES (?, ?, ?, ?, ?) " +
                        "RETURNING class_id";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setString(1, course);
            stmt.setInt(2, year);
            stmt.setString(3, term);
            stmt.setInt(4, section);
            stmt.setString(5, description);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("insert did not insert rows");
                }
                System.out.format("Added class %s%n", course);
            }
        }
    }

    @Command(name="select-class")
    public void selectClass(String course) throws SQLException
    {
        int count=0;
        String classQuery = "SELECT * FROM Class WHERE course_number = ?";
        String countQuery = "SELECT COUNT(*) FROM Class WHERE course_number = ?";
        try (PreparedStatement countStmt= db.prepareStatement(countQuery)){
            countStmt.setString(1,course);
            try (ResultSet rs = countStmt.executeQuery()) {
                while (rs.next()) {
                    count = rs.getInt("count");
                }
            }
            if(count==1) {
                try (PreparedStatement classStmt= db.prepareStatement(classQuery)) {
                    classStmt.setString(1,course);
                    try(ResultSet rs = classStmt.executeQuery()){
                        while(rs.next()){
                            activeClass = rs.getInt("class_id");
                            int section = rs.getInt("class_section");
                            System.out.println("Selected "+course+" Section "+section);
                        }
                    }
                }
            }
            else{
                System.out.println("Select Failed");
            }
        }
    }

    @Command(name="select-class")
    public void selectClass(String course, String term, int year) throws SQLException
    {
        int count=0;
        String classQuery = "SELECT * FROM Class WHERE course_number = ? AND class_term = ? AND class_year = ?";
        String countQuery = "SELECT COUNT(*) FROM Class WHERE course_number = ? AND class_term = ? AND class_year = ?";
        try (PreparedStatement countStmt= db.prepareStatement(countQuery)){
            countStmt.setString(1,course);
            countStmt.setString(2,term);
            countStmt.setInt(3,year);
            try (ResultSet rs = countStmt.executeQuery()) {
                while (rs.next()) {
                    count = rs.getInt("count");
                }
            }
            if(count==1) {
                try (PreparedStatement classStmt= db.prepareStatement(classQuery)) {
                    classStmt.setString(1,course);
                    classStmt.setString(2,term);
                    classStmt.setInt(3,year);
                    try(ResultSet rs = classStmt.executeQuery()){
                        while(rs.next()){
                            activeClass = rs.getInt("class_id");
                            int section = rs.getInt("class_section");
                            System.out.println("Selected "+course+" Section "+section);
                        }
                    }
                }
            }
            else{
                System.out.println("Select Failed");
            }
        }
    }

    @Command(name="select-class")
    public void selectClass(String course, String term, int year, String section) throws SQLException
    {
        int count=0;
        String classQuery = "SELECT * FROM Class WHERE course_number = ? AND class_term = ? AND class_year = ? AND class_section = ?";
        String countQuery = "SELECT COUNT(*) FROM Class WHERE course_number = ? AND class_term = ? AND class_year = ? AND class_section = ?";
        try (PreparedStatement countStmt= db.prepareStatement(countQuery)){
            countStmt.setString(1,course);
            countStmt.setString(2,term);
            countStmt.setInt(3,year);
            countStmt.setString(4,section);
            try (ResultSet rs = countStmt.executeQuery()) {
                while (rs.next()) {
                    count = rs.getInt("count");
                }
            }
            if(count==1) {
                try (PreparedStatement classStmt= db.prepareStatement(classQuery)) {
                    classStmt.setString(1,course);
                    classStmt.setString(2,term);
                    classStmt.setInt(3,year);
                    classStmt.setString(4,section);
                    try(ResultSet rs = classStmt.executeQuery()){
                        while(rs.next()){
                            activeClass = rs.getInt("class_id");
                            System.out.println("Selected "+course+" Section "+section);
                        }
                    }
                }
            }
            else{
                System.out.println("Select Failed");
            }
        }
    }

    @Command(name="show-class")
    public void showClass() throws SQLException {
        String classQuery = "SELECT * FROM Class WHERE class_id = ?";
        if (activeClass == 0)
            System.out.println("No class selected");
        else {
            try (PreparedStatement stmt = db.prepareStatement(classQuery)) {
                stmt.setInt(1,activeClass);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String course = rs.getString("course_number");
                        int year = rs.getInt("class_year");
                        String term = rs.getString("class_term");
                        int section = rs.getInt("class_section");
                        String description = rs.getString("class_description");
                        System.out.format("%s %d %s %d %s %n",course, year, term, section, description);
                    }
                }
            }

        }
    }

    @Command(name="add-category")
    public void addCategory(String name, double weight) throws SQLException{
        String query =
                "INSERT INTO Category(category_name, category_weight, class_id) VALUES (?,?,?)" +
                        "RETURNING category_id";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setDouble(2, weight);
            stmt.setInt(3, activeClass);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("Add category failed");
                }
                System.out.format("Added category %s%n", name);
            }
        }
    }

    @Command(name="show-categories")
    public void showCategories() throws SQLException{
        String query = "SELECT category_name,category_weight FROM Category WHERE class_id = ?";
        try(PreparedStatement stmt = db.prepareStatement(query)){
            stmt.setInt(1,activeClass);
            try (ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    String name= rs.getString("category_name");
                    double weight= rs.getDouble("category_weight");
                    System.out.format("%s %.2f %n",name, weight);
                }
            }
        }
    }

    @Command(name="add-item")
    public void addItem(String name,String category,String description, int points)throws SQLException {
        int categoryID = 0;
        String categoryQuery = "SELECT category_id FROM Category WHERE class_id = ? AND category_name = ?";
        try (PreparedStatement stmt2 = db.prepareStatement(categoryQuery)) {
            stmt2.setInt(1, activeClass);
            stmt2.setString(2, category);
            try (ResultSet rs = stmt2.executeQuery()) {
                while (rs.next()) {
                    categoryID = rs.getInt("category_id");
                }
            }
        }
        String itemInsert= "INSERT INTO Item(item_name, item_description, item_pointValue, category_id) VALUES (?,?,?,?)" +
                            "RETURNING item_id";
        try (PreparedStatement stmt3 = db.prepareStatement(itemInsert)) {
            stmt3.setString(1, name);
            stmt3.setString(2, description);
            stmt3.setInt(3, points);
            stmt3.setInt(4, categoryID);
            try (ResultSet rs = stmt3.executeQuery()) {
                if(!rs.next()){
                   System.out.println("Add item failed");
                }
                while (rs.next()) {
                    int itemID = rs.getInt("item_id");
                    System.out.format("Added item %s with id %d%n", name, itemID);
                }
            }
        }
    }

    @Command(name = "show-items")
    public void showItems()throws SQLException{
        int categoryID = 0;
        String itemQuery = "SELECT item_name, item_pointValue FROM Item JOIN Category USING (category_id) " +
        "WHERE class_id=? group by category_id,item_name,item_pointValue";
        try (PreparedStatement stmt2 = db.prepareStatement(itemQuery)) {
            stmt2.setInt(1, activeClass);
            try (ResultSet rs = stmt2.executeQuery()) {
                System.out.println("Assignment Value");
                while (rs.next()) {
                    String name= rs.getString("item_name");
                    int points = rs.getInt("item_pointvalue");
                    System.out.format("%s %d %n",name,points);
                }
            }
        }
    }

    @Command(name = "add-student")
    public void addStudent(String username, int studentid, String name)throws SQLException{
        String query = "INSERT INTO Student (student_id, student_name, student_username, class_id) VALUES (?,?,?,?) RETURNING student_id";
        try(PreparedStatement stmt = db.prepareStatement(query)){
            stmt.setInt(1,studentid);
            stmt.setString(2,name);
            stmt.setString(3,username);
            stmt.setInt(4,activeClass);
            try(ResultSet rs = stmt.executeQuery()){
                if(!rs.next()){
                    System.out.println("Add item failed");
                }
                while(rs.next()){
                    int studentID = rs.getInt("student_id");
                    System.out.format("Added item %s with id %d%n", name, studentID);
                }
            }
        }
    }

    @Command(name = "show-students")
    public void showStudent() throws SQLException{
   String query = "SELECT student_id,student_name,student_username FROM Student WHERE class_id = ?";
        try(PreparedStatement stmt = db.prepareStatement(query)){
            stmt.setInt(1,activeClass);
            try(ResultSet rs = stmt.executeQuery()){
                System.out.println("ID Name Username");
                while(rs.next()){
                    int studentID = rs.getInt("student_id");
                    String name = rs.getString("student_name");
                    String username = rs.getString("student_username");
                    System.out.printf("%d %s %s %n",studentID,name,username);
                }
            }
        }
    }

    @Command(name="show-students")
    public void showStudent(String s) throws SQLException{
        String query = "SELECT student_id,student_name,student_username FROM Student " +
                "WHERE class_id = ? " +
                "AND (student_name ILIKE ('%' || ? || '%') " +
                "OR student_username ILIKE ('%' || ? || '%'))";
        try(PreparedStatement stmt = db.prepareStatement(query)){
            stmt.setInt(1,activeClass);
            stmt.setString(2,s);
            stmt.setString(3,s);
            try(ResultSet rs = stmt.executeQuery()){
                System.out.println("ID name username");
                while(rs.next()){
                    int studentID = rs.getInt("student_id");
                    String name = rs.getString("student_name");
                    String username = rs.getString("student_username");
                    System.out.printf("%d %s %s %n",studentID,name,username);
                }
            }
        }
    }

    @Command(name = "grade")
    public void grade(String assignment, String username, double grade) throws SQLException {
        int studentID = 0;
        int itemID = 0;
        String queryStudent = "SELECT student_id FROM Student WHERE student_username = ?";
        try(PreparedStatement stmt = db.prepareStatement(queryStudent)){
            stmt.setString(1,username);
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    studentID = rs.getInt("student_id");
                }
            }
        }
        String queryItem = "SELECT item_id FROM Item JOIN Category USING (category_id) " +
                            "WHERE class_id = ? AND item_name = ?";
        try(PreparedStatement stmt = db.prepareStatement(queryItem)){
            stmt.setInt(1,activeClass);
            stmt.setString(2,assignment);
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    itemID = rs.getInt("item_id");
                }
            }
        }
        String queryGrade = "INSERT INTO student_assignment (student_id, item_id, grade) VALUES (?,?,?) RETURNING student_id";
        try(PreparedStatement stmt = db.prepareStatement(queryGrade)){
            stmt.setInt(1,studentID);
            stmt.setInt(2,itemID);
            stmt.setDouble(3,grade);
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    studentID = rs.getInt("student_id");
                }
            }
        }
    }

    @Command(name = "student-grades")
    public void studentGrades(String username) throws SQLException {
    int studentID = 0;
    double totalGrade = 0;
    double weightTotal = 0;
    ArrayList<String> categories = new ArrayList<String>();
    String categoryQuery = "SELECT category_name FROM Category WHERE class_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(categoryQuery)) {
        stmt.setInt(1,activeClass);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("category_name"));
            }
        }
    }
    String studentQuery = "SELECT student_id FROM Student WHERE student_username = ?";
        try (PreparedStatement stmt = db.prepareStatement(studentQuery)) {
        stmt.setString(1,username);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                studentID = rs.getInt("student_id");
            }
        }
    }
    String gradeQuery = "SELECT item_name, grade, item_pointValue " +
            "FROM student_assignment " +
            "JOIN Student USING (student_id) " +
            "JOIN Item USING  (item_id) " +
            "JOIN Category USING (category_id) " +
            "WHERE student_id = ? " +
            "AND Student.class_id  = ? " +
            "AND category_name = ?";
    String sumQuery = "SELECT SUM(grade) as attempt ,SUM(item_pointValue) as possible, category_weight " +
            "FROM student_assignment " +
            "JOIN Student USING (student_id) " +
            "JOIN Item USING  (item_id) " +
            "JOIN Category USING (category_id) " +
            "WHERE student_id = ? " +
            "AND Student.class_id  = ? " +
            "AND category_name = ? " +
            "GROUP BY category_weight";
        while(categories.size()>0) {
        try (PreparedStatement stmt = db.prepareStatement(gradeQuery)) {
            stmt.setInt(1, studentID);
            stmt.setInt(2, activeClass);
            stmt.setString(3, categories.get(categories.size()-1));
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println(categories.get(categories.size()-1)+" Category");
                System.out.println("Item Attempt Possible");
                while (rs.next()) {
                    String name = rs.getString("item_name");
                    Double attempt = rs.getDouble("grade");
                    Double possible = rs.getDouble("item_pointvalue");
                    System.out.format("%s %.2f %.2f %n", name, attempt,possible);
                }
            }
        }
        try(PreparedStatement stmt = db.prepareStatement(sumQuery)){
            stmt.setInt(1, studentID);
            stmt.setInt(2, activeClass);
            stmt.setString(3, categories.get(categories.size()-1));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Double attempt = rs.getDouble("attempt");
                    Double possible = rs.getDouble("possible");
                    Double weight = rs.getDouble("category_weight");
                    System.out.format("Total Attempted: %.2f Total Possible %.2f %n%n",attempt,possible);
                    totalGrade += weight *(attempt/possible);
                    weightTotal += weight;
                }
            }
        }
        categories.remove(categories.size()-1);

    }
        System.out.printf("Total Grade: %.2f %n",(totalGrade/weightTotal)*100);
}

    @Command(name = "gradebook")
    public void gradebook() throws SQLException{
        ArrayList<Integer> students = new ArrayList<Integer>();
        ArrayList<String> categories = new ArrayList<String>();
        String studentQuery = "SELECT student_id FROM Student WHERE class_id= ?";
        try (PreparedStatement stmt = db.prepareStatement(studentQuery)) {
            stmt.setInt(1,activeClass);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(rs.getInt("student_id"));
                }
            }
        }
        System.out.printf("Username ID Name Grade %n");
        while(students.size()>0) {
            int studentID = 0;
            String studentUsername = "";
            String studentName = "";
            double totalGrade = 0;
            double weightTotal = 0;
            studentID = students.get(students.size()-1);
            String categoryQuery = "SELECT category_name FROM Category WHERE class_id = ?";
            try (PreparedStatement stmt = db.prepareStatement(categoryQuery)) {
                stmt.setInt(1,activeClass);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        categories.add(rs.getString("category_name"));
                    }
                }
            }
            String nameQuery = "SELECT student_username, student_name FROM Student WHERE class_id = ? AND student_id = ?";
            try(PreparedStatement stmt = db.prepareStatement(nameQuery)){
                stmt.setInt(1,activeClass);
                stmt.setInt(2,studentID);
                try(ResultSet rs = stmt.executeQuery()){
                    while(rs.next()){
                        studentName = rs.getString("student_name");
                        studentUsername = rs.getString("student_username");
                    }
                }
            }
            String sumQuery = "SELECT SUM(grade) as attempt ,SUM(item_pointValue) as possible, category_weight " +
                    "FROM student_assignment " +
                    "JOIN Student USING (student_id) " +
                    "JOIN Item USING  (item_id) " +
                    "JOIN Category USING (category_id) " +
                    "WHERE student_id = ? " +
                    "AND Student.class_id  = ? " +
                    "AND category_name = ? " +
                    "GROUP BY category_weight";
            while (categories.size() > 0) {
                try (PreparedStatement stmt = db.prepareStatement(sumQuery)) {
                    stmt.setInt(1, studentID);
                    stmt.setInt(2, activeClass);
                    stmt.setString(3, categories.get(categories.size() - 1));
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Double attempt = rs.getDouble("attempt");
                            Double possible = rs.getDouble("possible");
                            Double weight = rs.getDouble("category_weight");
                            totalGrade += weight * (attempt / possible);
                            weightTotal += weight;
                        }
                    }
                }
                categories.remove(categories.size() - 1);
            }
            System.out.printf("%s %d %s %.2f %n",studentUsername,studentID,studentName,(totalGrade / weightTotal) * 100);
            students.remove(students.size()-1);
        }
    }
}


