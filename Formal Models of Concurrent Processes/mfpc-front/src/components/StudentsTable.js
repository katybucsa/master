import React from "react";
import DataTable from "react-data-table-component";
import {connect} from "react-redux";
import PropTypes from "prop-types";
import ArrowDownward from "@material-ui/icons/ArrowDownward";
import _ from "lodash";
import "./StudentsTable.css";
import {getStudents} from "../store/student/studentActions";
import FormAddStudent from "./FormAddStudent";
import StudentOptions from "./StudentOptions";

const columns = [
    {
        name: "Username",
        selector: row => row.username,
        sortable: true,
        ignoreRowClick: true,
        width: "31%"
    },
    {
        name: "First Name",
        selector: row => row.firstName,
        sortable: true,
        ignoreRowClick: true,
        width: "31%"
    },
    {
        name: "Last Name",
        selector: row => row.lastName,
        sortable: true,
        ignoreRowClick: true,
        width: "31%"
    },
    {
        selector: row => row.options,
        sortable: false,
        center: true,
        width: "7%"
    }
];
const sortIcon = <ArrowDownward/>;

const StudentsTable = ({
                           getStudents,
                           students,
                           loading,
                       }) => {
    const [anchorEl, setAnchorEl] = React.useState(null);

    // if (loading) {
    getStudents();
    // }

    const handleClick = event => {
        setAnchorEl(event.currentTarget);
    };

    const rows = _.map(students, item => {
        return {
            username: item.email,
            firstName: item.firstName,
            lastName: item.lastName,
            options: (
                <StudentOptions
                    studentId={item.studentId}
                    canDelete={item.noBorrows === 0}
                />
            )
        };
    });
    return (
        <>
            <FormAddStudent/>
            <DataTable
                title="Students"
                columns={columns}
                data={rows}
                sortIcon={sortIcon}
                onRowClicked={handleClick}
            />
        </>
    );
};
StudentsTable.propTypes = {
    getStudents: PropTypes.func.isRequired,
    loading: PropTypes.bool,
    students: PropTypes.array,
};

const mapStateToProps = state => ({
    students: state.students.data,
    loading: state.students.loading,
});

export default connect(mapStateToProps, {
    getStudents
})(StudentsTable);
