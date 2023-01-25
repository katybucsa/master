using CookComputing.XmlRpc;

namespace CSharpClient.proxy
{
    public struct Student
    {
        public int StudentId;
        public string FirstName;
        public string LastName;
        public string Email;
    }

    [XmlRpcUrl("http://localhost:8069/")]
    public interface IService : IXmlRpcProxy
    {
        [XmlRpcMethod("service.AddStudent")]
        void AddStudent(string firstName, string lastName, string email);

        [XmlRpcMethod("service.DeleteStudent")]
        void DeleteStudent(int studentId);

        [XmlRpcMethod("service.UpdateStudent")]
        void UpdateStudent(int studentId, string firstName, string lastName, string email);

        [XmlRpcMethod("service.GetAllStudents")]
        XmlRpcStruct[] GetAllStudents();

        [XmlRpcMethod("service.AddSubject")]
        void AddSubject(string code, string name);

        [XmlRpcMethod("service.DeleteSubject")]
        void DeleteSubject(string subjectId);

        [XmlRpcMethod("service.UpdateSubject")]
        void UpdateSubject(string subjectId, string name);

        [XmlRpcMethod("service.GetAllSubjects")]
        XmlRpcStruct[] GetAllSubjects();

        [XmlRpcMethod("service.AddGrade")]
        void AddGrade(int studentId, string subjectId, double value);

        [XmlRpcMethod("service.DeleteGrade")]
        void DeleteGrade(int gradeId);

        [XmlRpcMethod("service.UpdateGrade")]
        void UpdateGrade(int gradeId, double value);

        [XmlRpcMethod("service.GetAllGrades")]
        XmlRpcStruct[] GetAllGrades();
    }
}