using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace CSharpServer.domain
{
    [Table("subjects")]
    public class Subject
    {
        [Key, Column("subject_id")]
        public string SubjectId { get; set; }
        
        [Required, Column("name"), MaxLength(255)]
        public string Name { get; set; }
        
        public IList<Grade> Grades { get; set; }
    }
}