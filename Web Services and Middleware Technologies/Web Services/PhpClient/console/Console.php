<?php


use PhpXmlRpc\Client;
use PhpXmlRpc\Encoder;
use PhpXmlRpc\Request;
use PhpXmlRpc\Value;

error_reporting(E_ERROR | E_PARSE);

class Console
{
    private Client $proxy;

    public function __construct(Client $client)
    {
        $this->proxy = $client;
    }

    private function print_menu()
    {
        echo "\n0. Exit\n";
        echo "1. Add student\n";
        echo "2. Delete student\n";
        echo "3. Update student\n";
        echo "4. Add subject\n";
        echo "5. Delete subject\n";
        echo "6. Update subject\n";
        echo "7. Add grade\n";
        echo "8. Delete grade\n";
        echo "9. Update grade\n";
        echo "10. Show students\n";
        echo "11. Show subjects\n";
        echo "12. Show grades\n";
    }

    private function add_student()
    {
        $first_name = readline("Insert student first name: ");
        $last_name = readline("Insert student last name: ");
        $email = readline("Insert student email: ");
        try {
            $message = new Request("service.AddStudent", array(new Value($first_name), new Value($last_name), new Value($email)));
            $this->proxy->send($message);

            echo "Student added successfully!\n";
        } catch (Exception $e) {
            echo "Student could not be added!\n" . $e->getMessage() . "\n";
        }
    }

    private function delete_student()
    {
        while (true) {
            try {
                $student_id = (int)readline("Insert student id you want to delete: ");
                break;
            } catch (Exception $e) {
                echo "Student id must be integer!\n";
            }
        }
        try {
            $message = new Request("service.DeleteStudent", array(new Value($student_id, "int")));
            $this->proxy->send($message);
            echo "Student deleted successfully!\n";
        } catch (Exception $exception) {
            echo "Student with id " . $student_id . " does not exist or could not be deleted!\n";
        }
    }

    private function update_student()
    {
        $student_id = (int)readline("Insert student id you want to update: ");
        $firstN_name = readline("Insert student new first name (leave blank to not modify): ");
        $last_name = readline("Insert student new last name (leave blank to not modify): ");
        $email = readline("Insert student new email (leave blank to not modify): ");

        try {
            $message = new Request("service.UpdateStudent",
                array(new Value($student_id, "int"), new Value($firstN_name), new  Value($last_name), new Value($email)));
            $this->proxy->send($message);
            echo "Student updated successfully!\n";
        } catch (Exception $exception) {
            echo "Student with id " . $student_id . " does not exist or student could not be updated!\n";
        }
    }

    private function add_subject()
    {
        $id = readline("Insert subject code: ");
        $name = readline("Insert subject name: ");
        try {
            $message = new Request("service.AddSubject", array(new Value($id), new Value($name)));
            $this->proxy->send($message);
            echo "Subject added successfully!\n";
        } catch (Exception $exception) {
            echo "Subject could not be added!\n";
        }
    }

    private function delete_subject()
    {
        $subject_id = readline("Insert subject id you want tot delete: ");
        try {
            $message = new Request("service.DeleteSubject", array(new Value($subject_id)));
            $this->proxy->send($message);
            echo "Subject deleted successfully!\n";
        } catch (Exception $exception) {
            echo "Subject with id: " . $subject_id . " does not exist or could not be deleted!\n";
        }
    }

    private function update_subject()
    {
        $subject_id = readline("Insert subject id you want to update: ");
        $name = readline("Insert new subject name: ");
        if (empty($name)) {
            echo "New subject name is empty! Please try again with a non empty string!\n";
            return;
        }
        try {
            $message = new Request("service.UpdateSubject", array(new Value($subject_id), new Value($name)));
            $this->proxy->send($message);
            echo "Subject updated successfully!\n";
        } catch (Exception $exception) {
            echo "Subject with id " . $subject_id . " does not exist or subject could not be updated!\n";
        }
    }

    private function add_grade()
    {
        while (true) {
            try {
                $student_id = (int)readline("Insert student id you want to add the grade: ");
                $subject_id = readline("Insert subject id you want to add the grade: ");
                break;
            } catch (Exception $exception) {
                echo "Student id must be integer!\n";
            }
        }
        while (true) {
            try {
                $value = (float)readline("Insert the grade: ");
                break;
            } catch (Exception $exception) {
                echo "Grade must be a float number!\n";
            }
        }
        try {
            $message = new Request("service.AddGrade", array(new Value($student_id, "int"), new Value($subject_id), new Value($value, "double")));
            $this->proxy->send($message);
            echo "Grade added successfully!\n";
        } catch (Exception $exception) {
            echo "Grade could not be added!\n";
        }
    }

    private function delete_grade()
    {
        while (true) {
            try {
                $grade_id = (int)readline("Insert grade id you want to delete: ");
                break;
            } catch (Exception $exception) {
                echo "Grade id must be integer!\n";
            }
        }
        try {
            $message = new Request("service.DeleteGrade", array(new Value($grade_id, "int")));
            $this->proxy->send($message);
            echo "Grade deleted successfully!\n";
        } catch (Exception $exception) {
            echo "Grade with id " . $grade_id . " does not exist or could not be deleted!\n";
        }
    }

    private function update_grade()
    {
        while (true) {
            try {
                $grade_id = (int)readline("Insert grade id for the grade you want tot update: ");
                $new_value = (float)readline("Insert the new grade: ");
                break;
            } catch (Exception $exception) {
                echo "Grade id must be integer and grade must be a float number!\n";
            }
        }
        try {
            $message = new Request("service.UpdateGrade", array(new Value($grade_id, "int"), new Value($new_value, "double")));
            $this->proxy->send($message);
            echo "Grade updated successfully!\n";
        } catch (Exception $exception) {
            echo "Grade with id " . $grade_id . " does not exist or grade could not be updated!\n";
        }
    }

    private function show_students()
    {
        $message = new Request("service.GetAllStudents");
        $response = $this->proxy->send($message);
        $encoder = new Encoder();
        $arr = $encoder->decode($response->value());
        if (count($arr) > 0)
            echo "\n================================Students list================================\n";
        else
            echo "\n==========There is no student in the list==========\n";
        foreach ($arr as $r)
            echo "Id: " . $r["studentId"] . ", first name: " . $r["firstName"] . ",  last name: " . $r["lastName"] . ", email: " . $r["email"] . "\n";
        echo "\n";
    }

    private function show_subjects()
    {
        $message = new Request("service.GetAllSubjects");
        $response = $this->proxy->send($message);
        $encoder = new Encoder();
        $arr = $encoder->decode($response->value());
        if (count($arr) > 0)
            echo "\n================================Subjects list================================\n";
        else
            echo "\n==========There is no subject in the list==========\n";
        foreach ($arr as $r)
            echo "Id: " . $r["subjectId"] . ", name: " . $r["name"] . "\n";
        echo "\n";
    }

    private function show_grades()
    {
        $message = new Request("service.GetAllGrades");
        $response = $this->proxy->send($message);
        $encoder = new Encoder();
        $arr = $encoder->decode($response->value());
        if (count($arr) > 0)
            echo "\n================================Grades list================================\n";
        else
            echo "\n==========There is no grade in the list==========\n";
        foreach ($arr as $r)
            echo "Id: " . $r["gradeId"] . ", student id: " . $r["studentId"] . ", subject id: " . $r["subjectId"] . ", grade: " . $r["value"] . "\n";
        echo "\n";
    }

    public function run()
    {
        while (true) {
            $this->print_menu();
            $cmd = readline("Insert command: ");
            switch ($cmd) {
                case "0":
                    return;
                case "1":
                    $this->add_student();
                    break;
                case "2":
                    $this->delete_student();
                    break;
                case "3":
                    $this->update_student();
                    break;
                case "4":
                    $this->add_subject();
                    break;
                case "5":
                    $this->delete_subject();
                    break;
                case "6":
                    $this->update_subject();
                    break;
                case "7":
                    $this->add_grade();
                    break;
                case "8":
                    $this->delete_grade();
                    break;
                case "9":
                    $this->update_grade();
                    break;
                case "10":
                    $this->show_students();
                    break;
                case "11":
                    $this->show_subjects();
                    break;
                case "12":
                    $this->show_grades();
                    break;
                default:
                    echo "Invalid command!\n";
                    break;
            }
        }
    }
}