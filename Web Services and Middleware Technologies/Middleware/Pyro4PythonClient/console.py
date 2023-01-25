import json


class Command:
    def __init__(self, cmd_name, cmd_method):
        self.__cmd_name = cmd_name
        self.__cmd_method = cmd_method

    def execute(self):
        self.__cmd_method()


class Console:

    def __init__(self, proxy):
        self.__proxy = proxy

    def print_menu(self):
        print('0. Exit')
        print('1. Add student')
        print('2. Delete student')
        print('3. Update student')
        print('4. Add subject')
        print('5. Delete subject')
        print('6. Update subject')
        print('7. Add grade')
        print('8. Update grade')
        print('9. Show students')
        print('10. Show subjects')
        print('11. Show grades')

    def __add_student(self):
        first_name = input('Insert student first name: ')
        last_name = input('Insert student last name: ')
        email = input('Insert student email: ')
        try:
            self.__proxy.add_student(first_name, last_name, email)
            print('Student added successfully!\n')
        except:
            print('Student could not be added!\n')

    def __delete_student(self):
        while True:
            try:
                student_id = int(input('Insert student id you want to delete: '))
                break
            except:
                print('Student id must be integer!\n')
        try:
            self.__proxy.delete_student(student_id)
            print('Student deleted successfully!\n')
        except:
            print('Student with id: ', student_id, ' does not exist or could not be deleted!\n')

    def __update_student(self):
        student_id = int(input('Insert student id you want to update: '))
        first_name = input('Insert student new first name (leave blank to not modify): ')
        last_name = input('Insert student new last name (leave blank to not modify): ')
        email = input('Insert student new email (leave blank to not modify): ')
        try:
            self.__proxy.update_student(student_id, first_name, last_name, email)
            print('Student with id ', student_id, ' successfully updated!\n')
        except:
            print('Student with id ', student_id, ' does not exist or student could not be updated!\n')

    def __add_subject(self):
        id = input('Insert subject code: ')
        name = input('Insert subject name: ')
        try:
            self.__proxy.add_subject(id, name)
            print('Subject added successfully!\n')
        except:
            print('Subject could not be added!\n')

    def __delete_subject(self):

        subject_id = input('Insert subject id you want tot delete: ')
        try:
            self.__proxy.delete_subject(subject_id)
            print('Subject deleted successfully!\n')
        except:
            print('Subject with id: ', subject_id, ' does not exist or could not be deleted!\n')

    def __update_subject(self):
        subject_id = input('Insert subject id you want to update: ')
        name = input('Insert new subject name: ')
        if not name:
            print('New subject name is empty! Please try again with a non empty string!\n')
            return
        try:
            self.__proxy.update_subject(subject_id, name)
            print('Subject updated successfully!\n')
        except:
            print('Subject with id ', subject_id, ' does not exist or subject could not be updated!\n')

    def __add_grade(self):
        while True:
            try:
                student_id = int(input('Insert student id you want to add the grade: '))
                subject_id = input('Insert subject id you want to add the grade: ')
                break
            except:
                print('Student id must be integer!\n')
        while True:
            try:
                value = float(input('Insert the grade: '))
                break
            except:
                print('Grade must be a float number!\n')
        try:
            self.__proxy.add_grade(student_id, subject_id, value)
            print('Grade added successfully!\n')
        except:
            print('Grade could not be added!\n')

    def __update_grade(self):
        while True:
            try:
                grade_id = int(input('Insert grade id for the grade you want tot update: '))
                value = float(input('Insert the new grade: '))
                break
            except:
                print('Grade id must be integer and grade must be a float number!\n')
        try:
            self.__proxy.update_grade(grade_id, value)
            print('Grade updated successfully!\n')
        except:
            print('Grade with id ', grade_id, ' does not exist or grade could not be updated!\n')

    def __show_students(self):
        json_object = self.__proxy.get_all_students()
        data = json.loads(json_object)['data']
        if len(data) > 0:
            print('\n==========Students list==========')
        else:
            print('\n==========There is no student in the list==========\n')
        for elem in data:
            s = json.loads(elem)
            print('Id: ', str(s['id']), ', first name: ', s['first_name'],
                  ', last name: ', s['last_name'], ', email: ', s['email'])
        print('\n')

    def __show_subjects(self):
        json_object = self.__proxy.get_all_subjects()
        data = json.loads(json_object)['data']
        if len(data) > 0:
            print('\n==========Subjects list==========')
        else:
            print('\n==========There is no subject in the list==========\n')
        for elem in data:
            s = json.loads(elem)
            print('Id: ', s['id'], ', name: ', s['name'])
        print('\n')

    def __show_grades(self):
        json_object = self.__proxy.get_all_grades()
        data = json.loads(json_object)['data']
        if len(data) > 0:
            print('\n==========Grades list==========')
        else:
            print('\n==========There is no grade in the list==========\n')
        for elem in data:
            g = json.loads(elem)
            print('Id: ', str(g['id']), ', student id: ', str(g['student_id']), ', subject_id: ',
                  str(g['subject_id']), ', grade: ', str(g['value']))
        print('\n')

    def run(self):
        commands = {'1': Command('1', self.__add_student),
                    '2': Command('2', self.__delete_student),
                    '3': Command('3', self.__update_student),
                    '4': Command('4', self.__add_subject),
                    '5': Command('5', self.__delete_subject),
                    '6': Command('6', self.__update_subject),
                    '7': Command('7', self.__add_grade),
                    '8': Command('8', self.__update_grade),
                    '9': Command('9', self.__show_students),
                    '10': Command('10', self.__show_subjects),
                    '11': Command('11', self.__show_grades)}
        while True:
            self.print_menu()
            print('Insert command: ')
            cmd = input('>>')
            if cmd == '0':
                return
            if cmd in commands:
                commands[cmd].execute()
            else:
                print('Invalid command!\n')
