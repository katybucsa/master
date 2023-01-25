using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace CSharpServer.domain
{
    [Table("grades")]
    public class Grade
    {
        [Key, Column("grade_id"), DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int GradeId { get; set; }
        
        [Column("student_id"), ForeignKey("Student")]
        public int StudentId { get; set; }
        public Student Student { get; set; }
        
        [Column("subject_id"), ForeignKey("Subject")]
        public string SubjectId { get; set; }
        public Subject Subject { get; set; }
        
        [Column("value"), Required]
        public float Value { get; set; }
    }
}