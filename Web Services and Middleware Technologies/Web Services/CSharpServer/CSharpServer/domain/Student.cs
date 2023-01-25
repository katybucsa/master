using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace CSharpServer.domain
{
    [Table("students")]
    [Serializable]
    public class Student
    {
        [Key, Column("student_id"),DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int StudentId { get; set; }
        
        [Required, Column("first_name"), MaxLength(32)]
        public string FirstName { get; set; }
        
        [Required, Column("last_name"), MaxLength(32)]
        public string LastName { get; set; }
        
        [Required, Column("email"), MaxLength(320)]
        public string Email { get; set; }
        
        public IList<Grade> Grades { get; set; }
    }
}