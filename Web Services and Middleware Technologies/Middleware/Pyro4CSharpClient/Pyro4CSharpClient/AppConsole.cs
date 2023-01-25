using System;
using System.Text.Json;
using Razorvine.Pyro;

namespace Pyro4CSharpClient
{
    public class AppConsole
    {
        private readonly PyroProxy _proxy;

        public AppConsole(PyroProxy proxy)
        {
            this._proxy = proxy;
        }

        private void PrintMenu()
        {
            Console.WriteLine("0. Exit");
            Console.WriteLine("1. Add student");
            Console.WriteLine("2. Delete student");
            Console.WriteLine("3. Update student");
            Console.WriteLine("4. Add subject");
            Console.WriteLine("5. Delete subject");
            Console.WriteLine("6. Update subject");
            Console.WriteLine("7. Add grade");
            Console.WriteLine("8. Update grade");
            Console.WriteLine("9. Show students");
            Console.WriteLine("10. Show subjects");
            Console.WriteLine("11. Show grades");
        }

        private void AddStudent()
        {
            Console.Write("Insert student first name: ");
            var firstName = Console.ReadLine();
            Console.Write("Insert student last name: ");
            var lastName = Console.ReadLine();
            Console.Write("Insert student email: ");
            var email = Console.ReadLine();
            try
            {
                this._proxy.call("add_student", firstName, lastName, email);
                Console.WriteLine("Student added successfully!\n");
            }
            catch (Exception)
            {
                Console.WriteLine("Student could not be added!\n");
            }
        }

        private void DeleteStudent()
        {
            int studentId;
            while (true)
            {
                try
                {
                    Console.Write("Insert student id you want to delete: ");
                    studentId = Convert.ToInt32(Console.ReadLine());
                    break;
                }
                catch (Exception)
                {
                    Console.WriteLine("Student id must be integer!\n");
                }
            }

            try
            {
                this._proxy.call("delete_student", studentId);
                Console.WriteLine("Student deleted successfully!\n");
            }
            catch (Exception)
            {
                Console.WriteLine("Student with id " + studentId + " does not exist or could not be deleted!\n");
            }
        }

        private void UpdateStudent()
        {
            Console.Write("Insert student id you want to update: ");
            var studentId = Convert.ToInt32(Console.ReadLine());
            Console.Write("Insert student new first name (leave blank to not modify): ");
            var firstName = Console.ReadLine();
            Console.Write("Insert student new last name (leave blank to not modify): ");
            var lastName = Console.ReadLine();
            Console.Write("Insert student new email (leave blank to not modify): ");
            var email = Console.ReadLine();
            try
            {
                this._proxy.call("update_student", studentId, firstName, lastName, email);
                Console.WriteLine("Student updated successfully!\n");
            }
            catch (Exception)
            {
                Console.WriteLine("Student with id " + studentId +
                                  " does not exist or student could not be updated!\n");
            }
        }

        private void AddSubject()
        {
            Console.Write("Insert subject code: ");
            var id = Console.ReadLine();
            Console.Write("Insert subject name: ");
            var name = Console.ReadLine();
            try
            {
                this._proxy.call("add_subject", id, name);
                Console.WriteLine("Subject added successfully!\n");
            }
            catch (Exception)
            {
                Console.WriteLine("Subject could not be added!\n");
            }
        }

        private void DeleteSubject()
        {
            Console.Write("Insert subject id you want tot delete: ");
            var subjectId = Console.ReadLine();
            try
            {
                this._proxy.call("delete_subject", subjectId);
                Console.WriteLine("Subject deleted successfully!\n");
            }
            catch (Exception)
            {
                Console.WriteLine("Subject with id: " + subjectId + " does not exist or could not be deleted!\n");
            }
        }

        private void UpdateSubject()
        {
            Console.Write("Insert subject id you want to update: ");
            var subjectId = Console.ReadLine();
            Console.Write("Insert new subject name: ");
            var name = Console.ReadLine();
            if (string.IsNullOrEmpty(name))
            {
                Console.WriteLine("New subject name is empty! Please try again with a non empty string!\n");
                return;
            }

            try
            {
                this._proxy.call("update_subject", subjectId, name);
                Console.WriteLine("Subject updated successfully!\n");
            }
            catch (Exception)
            {
                Console.WriteLine(
                    "Subject with id " + subjectId + " does not exist or subject could not be updated!\n");
            }
        }

        private void AddGrade()
        {
            int studentId;
            string subjectId;
            float value;
            while (true)
            {
                try
                {
                    Console.Write("Insert student id you want to add the grade: ");
                    studentId = Convert.ToInt32(Console.ReadLine());
                    Console.Write("Insert subject id you want to add the grade: ");
                    subjectId = Console.ReadLine();
                    break;
                }
                catch (Exception)
                {
                    Console.WriteLine("Student id must be integer!\n");
                }
            }

            while (true)
            {
                try
                {
                    Console.Write("Insert the grade: ");
                    value = float.Parse(Console.ReadLine() ?? string.Empty);
                    break;
                }
                catch (Exception)
                {
                    Console.WriteLine("Grade must be a float number!\n");
                }
            }

            try
            {
                this._proxy.call("add_grade", studentId, subjectId, value);
                Console.WriteLine("Grade added successfully!\n");
            }
            catch (Exception)
            {
                Console.WriteLine("Grade could not be added!\n");
            }
        }

        private void UpdateGrade()
        {
            int gradeId;
            float value;
            while (true)
            {
                try
                {
                    Console.Write("Insert grade id for the grade you want tot update: ");
                    gradeId = Convert.ToInt32(Console.ReadLine());
                    Console.Write("Insert the new grade: ");
                    value = float.Parse(Console.ReadLine() ?? string.Empty);
                    break;
                }
                catch (Exception)
                {
                    Console.WriteLine("Grade id must be integer and grade must be a float number!\n");
                }
            }

            try
            {
                this._proxy.call("update_grade", gradeId, value);
                Console.WriteLine("Grade updated successfully!\n");
            }
            catch (Exception)
            {
                Console.WriteLine("'Grade with id " + gradeId + " does not exist or grade could not be updated!\n");
            }
        }

        private void ShowStudents()
        {
            var jsonDocument = JsonDocument.Parse((string) this._proxy.call("get_all_students"));
            var rootElement = jsonDocument.RootElement.GetProperty("data");
            Console.WriteLine(rootElement.GetArrayLength() > 0
                ? "\n==========Students list=========="
                : "\n==========There is no student in the list==========\n");
            for (var i = 0; i < rootElement.GetArrayLength(); i++)
            {
                var elem = JsonDocument.Parse(rootElement[i].GetString()).RootElement;
                Console.WriteLine("Id: " + elem.GetProperty("id") + ", first name: " + elem.GetProperty("first_name") +
                                  ",  last name: " + elem.GetProperty("last_name") + ", email: " +
                                  elem.GetProperty("email"));
            }

            Console.WriteLine();
        }

        private void ShowSubjects()
        {
            var jsonDocument = JsonDocument.Parse((string) this._proxy.call("get_all_subjects"));
            var rootElement = jsonDocument.RootElement.GetProperty("data");
            Console.WriteLine(rootElement.GetArrayLength() > 0
                ? "\n==========Subjects list=========="
                : "\n==========There is no subject in the list==========\n");
            for (var i = 0; i < rootElement.GetArrayLength(); i++)
            {
                var elem = JsonDocument.Parse(rootElement[i].GetString()).RootElement;
                Console.WriteLine("Id: " + elem.GetProperty("id") + ", name: " + elem.GetProperty("name"));
            }

            Console.WriteLine();
        }

        private void ShowGrades()
        {
            var jsonDocument = JsonDocument.Parse((string) this._proxy.call("get_all_grades"));
            var rootElement = jsonDocument.RootElement.GetProperty("data");
            Console.WriteLine(rootElement.GetArrayLength() > 0
                ? "\n==========Grades list=========="
                : "\n==========There is no grade in the list==========\n");
            for (var i = 0; i < rootElement.GetArrayLength(); i++)
            {
                var elem = JsonDocument.Parse(rootElement[i].GetString()).RootElement;
                Console.WriteLine("Id: " + elem.GetProperty("id") + ", student id: " + elem.GetProperty("student_id") +
                                  ", subject id: " + elem.GetProperty("subject_id") +
                                  ", grade: " + elem.GetProperty("value"));
            }

            Console.WriteLine();
        }

        public void Run()
        {
            while (true)
            {
                PrintMenu();
                Console.Write("Insert command: ");
                var cmd = Console.ReadLine();
                switch (cmd)
                {
                    case "0":
                        return;
                    case "1":
                        this.AddStudent();
                        break;
                    case "2":
                        this.DeleteStudent();
                        break;
                    case "3":
                        this.UpdateStudent();
                        break;
                    case "4":
                        this.AddSubject();
                        break;
                    case "5":
                        this.DeleteSubject();
                        break;
                    case "6":
                        this.UpdateSubject();
                        break;
                    case "7":
                        this.AddGrade();
                        break;
                    case "8":
                        this.UpdateGrade();
                        break;
                    case "9":
                        this.ShowStudents();
                        break;
                    case "10":
                        this.ShowSubjects();
                        break;
                    case "11":
                        this.ShowGrades();
                        break;
                    default:
                        Console.WriteLine("Invalid command!\n");
                        break;
                }
            }
        }
    }
}