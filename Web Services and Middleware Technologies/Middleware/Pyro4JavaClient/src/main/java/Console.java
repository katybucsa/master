import net.razorvine.pyro.PyroProxy;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Scanner;

public class Console {

    private final PyroProxy proxy;
    private final Scanner scanner;

    public Console(PyroProxy proxy) {

        this.proxy = proxy;
        scanner = new Scanner(System.in);
    }

    private void printMenu() {

        System.out.println("0. Exit");
        System.out.println("1. Add student");
        System.out.println("2. Delete student");
        System.out.println("3. Update student");
        System.out.println("4. Add subject");
        System.out.println("5. Delete subject");
        System.out.println("6. Update subject");
        System.out.println("7. Add grade");
        System.out.println("8. Update grade");
        System.out.println("9. Show students");
        System.out.println("10. Show subjects");
        System.out.println("11. Show grades");
    }

    private void addStudent() {

        System.out.print("Insert student first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Insert student last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Insert student email: ");
        String email = scanner.nextLine();
        try {
            this.proxy.call("add_student", firstName, lastName, email);
            System.out.println("Student added successfully!\n");
        } catch (Exception e) {
            System.out.println("Student could not be added!\n");
        }
    }

    private void deleteStudent() {

        int studentId;
        while (true) {
            try {
                System.out.print("Insert student id you want to delete: ");
                studentId = scanner.nextInt();
                break;
            } catch (Exception e) {
                System.out.println("Student id must be integer!\n");
            }
        }
        try {
            this.proxy.call("delete_student", studentId);
            System.out.println("Student deleted successfully!\n");
        } catch (Exception e) {
            System.out.println("Student with id " + studentId + " does not exist or could not be deleted!\n");
        }
    }

    private void updateStudent() {

        System.out.print("Insert student id you want to update: ");
        int studentId = scanner.nextInt();
        System.out.print("Insert student new first name (leave blank to not modify): ");
        scanner.nextLine();
        String firstName = scanner.nextLine();
        System.out.print("Insert student new last name (leave blank to not modify): ");
        String lastName = scanner.nextLine();
        System.out.print("Insert student new email (leave blank to not modify): ");
        String email = scanner.nextLine();
        try {
            this.proxy.call("update_student", studentId, firstName, lastName, email);
            System.out.println("Student updated successfully!\n");
        } catch (Exception e) {
            System.out.println("Student with id " + studentId + " does not exist or student could not be updated!\n");
        }

    }

    private void addSubject() {

        System.out.print("Insert subject code: ");
        String id = scanner.nextLine();
        System.out.print("Insert subject name: ");
        String name = scanner.nextLine();
        try {
            this.proxy.call("add_subject", id, name);
            System.out.println("Subject added successfully!\n");
        } catch (Exception e) {
            System.out.println("Subject could not be added!\n");
        }
    }

    private void deleteSubject() {

        System.out.print("Insert subject id you want tot delete: ");
        String subjectId = scanner.nextLine();
        try {
            this.proxy.call("delete_subject", subjectId);
            System.out.println("Subject deleted successfully!\n");
        } catch (Exception e) {
            System.out.println("Subject with id: " + subjectId + " does not exist or could not be deleted!\n");
        }
    }

    private void updateSubject() {
        System.out.print("Insert subject id you want to update: ");
        String subjectId = scanner.nextLine();
        System.out.print("Insert new subject name: ");
        String name = scanner.nextLine();
        if (name.isEmpty()) {
            System.out.println("New subject name is empty! Please try again with a non empty string!\n");
            return;
        }
        try {
            this.proxy.call("update_subject", subjectId, name);
            System.out.println("Subject updated successfully!\n");
        } catch (Exception e) {
            System.out.println("Subject with id " + subjectId + " does not exist or subject could not be updated!\n");
        }
    }

    private void addGrade() {
        int studentId;
        String subjectId;
        float value;
        while (true) {
            try {
                System.out.print("Insert student id you want to add the grade: ");
                studentId = scanner.nextInt();
                System.out.print("Insert subject id you want to add the grade: ");
                scanner.nextLine();
                subjectId = scanner.nextLine();
                break;
            } catch (Exception e) {
                System.out.println("Student id must be integer!\n");
            }
        }
        while (true) {
            try {
                System.out.print("Insert the grade: ");
                value = scanner.nextFloat();
                break;
            } catch (Exception e) {
                System.out.println("Grade must be a float number!\n");
            }
        }
        try {
            this.proxy.call("add_grade", studentId, subjectId, value);
            System.out.println("Grade added successfully!\n");
        } catch (Exception e) {
            System.out.println("Grade could not be added!\n");
        }
    }

    private void updateGrade() {
        int gradeId;
        float value;
        while (true) {
            try {
                System.out.print("Insert grade id for the grade you want tot update: ");
                gradeId = scanner.nextInt();
                System.out.print("Insert the new grade: ");
                value = scanner.nextFloat();
                break;
            } catch (Exception e) {
                System.out.println("Grade id must be integer and grade must be a float number!\n");
            }
        }
        try {
            this.proxy.call("update_grade", gradeId, value);
            System.out.println("Grade updated successfully!\n");
        } catch (Exception e) {
            System.out.println("'Grade with id " + gradeId + " does not exist or grade could not be updated!\n");
        }
    }

    private void showStudents() throws IOException {
        JSONObject jsonObject = new JSONObject((String) this.proxy.call("get_all_students"));
        JSONArray data = (JSONArray) jsonObject.get("data");
        if (data.length() > 0)
            System.out.println("\n==========Students list==========");
        else
            System.out.println("\n==========There is no student in the list==========\n");
        for (Object o : data) {
            JSONObject s = new JSONObject((String) o);
            System.out.println("Id: " + s.get("id") + ", first name: " + s.get("first_name") + ",  last name: " + s.get("last_name") + ", email: " + s.get("email"));
        }
        System.out.println();
    }

    private void showSubjects() throws IOException {

        JSONObject jsonObject = new JSONObject((String) this.proxy.call("get_all_subjects"));
        JSONArray data = (JSONArray) jsonObject.get("data");
        if (data.length() > 0)
            System.out.println("\n==========Subjects list==========");
        else
            System.out.println("\n==========There is no subject in the list==========\n");
        for (Object o : data) {
            JSONObject s = new JSONObject((String) o);
            System.out.println("Id: " + s.get("id") + ", name: " + s.get("name"));
        }
        System.out.println();
    }

    private void showGrades() throws IOException {

        JSONObject jsonObject = new JSONObject((String) this.proxy.call("get_all_grades"));
        JSONArray data = (JSONArray) jsonObject.get("data");
        if (data.length() > 0)
            System.out.println("\n==========Grades list==========");
        else
            System.out.println("\n==========There is no grade in the list==========\n");
        for (Object o : data) {
            JSONObject g = new JSONObject((String) o);
            System.out.println("Id: " + g.get("id") + ", student id: " + g.get("student_id") + ", subject id: " + g.get("subject_id") + ", grade: " + g.get("value"));
        }
        System.out.println();
    }

    public void run() throws IOException {
        while (true) {
            printMenu();
            System.out.print("Insert command: ");
            String cmd = scanner.nextLine();
            switch (cmd) {
                case "0":
                    return;
                case "1":
                    this.addStudent();
                    break;
                case "2":
                    this.deleteStudent();
                    break;
                case "3":
                    this.updateStudent();
                    break;
                case "4":
                    this.addSubject();
                    break;
                case "5":
                    this.deleteSubject();
                    break;
                case "6":
                    this.updateSubject();
                    break;
                case "7":
                    this.addGrade();
                    break;
                case "8":
                    this.updateGrade();
                    break;
                case "9":
                    this.showStudents();
                    break;
                case "10":
                    this.showSubjects();
                    break;
                case "11":
                    this.showGrades();
                    break;
                default:
                    System.out.println("Invalid command!\n");
                    break;
            }
        }
    }
}
