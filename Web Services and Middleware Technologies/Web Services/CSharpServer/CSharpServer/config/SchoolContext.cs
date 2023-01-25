using CSharpServer.domain;
using Microsoft.EntityFrameworkCore;

namespace CSharpServer.config
{
    public class SchoolContext : DbContext
    {
        public DbSet<Student> Students { get; set; }
        public DbSet<Subject> Subjects { get; set; }
        public DbSet<Grade> Grades { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseNpgsql(
                "Server=postgresql;Host=localhost;Port=5432;User Id=postgres;Password=postgres;Database=mini-web");
        }
    }
}