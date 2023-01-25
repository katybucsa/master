import React from "react";
import DataTable from "react-data-table-component";
import {connect} from "react-redux";
import PropTypes from "prop-types";
import ArrowDownward from "@material-ui/icons/ArrowDownward";
import _ from "lodash";
import "./StudentsTable.css";
import FormAddStudent from "./FormAddStudent";
import {getStudentBorrowedBooks} from "../store/borrowedBooks/borrowedBooksActions";
import {useNavigate, useParams} from "react-router-dom";

const columns = [
    {
        name: "Book Name",
        selector: row => row.name,
        sortable: true,
        ignoreRowClick: true,
        width: "33%"
    },
    {
        name: "Borrow Date",
        selector: row => row.borrow_date,
        sortable: true,
        ignoreRowClick: true,
        width: "33%"
    },
    {
        name: "Return Date",
        selector: row => row.return_date,
        sortable: true,
        ignoreRowClick: true,
        width: "33%"
    },
];
const sortIcon = <ArrowDownward/>;

const StudentBorrowedBooks = ({
                                  getStudentBorrowedBooks,
                                  books,
                                  match
                              }) => {
    const params = useParams();
    const [anchorEl, setAnchorEl] = React.useState(null);
    const studentId = params.sId;
    const history = useNavigate();

    getStudentBorrowedBooks(studentId, history);

    const handleClick = event => {
        setAnchorEl(event.currentTarget);
    };

    const rows = _.map(books, item => {
        return {
            name: item.bookName,
            borrow_date: new Date(item.borrowDate).toString(),
            return_date: item.returnDate !== null ? new Date(item.returnDate).toString() : null
        };
    });
    return (
        <>
            <DataTable
                title={""}
                columns={columns}
                data={rows}
                sortIcon={sortIcon}
                onRowClicked={handleClick}
            />
        </>
    );
};
StudentBorrowedBooks.propTypes = {
    getStudentBorrowedBooks: PropTypes.func.isRequired,
    loading: PropTypes.bool,
    books: PropTypes.array,
};

const mapStateToProps = state => ({
    books: state.borrowedBooks.data,
    loading: state.borrowedBooks.loading,
});

export default connect(mapStateToProps, {
    getStudentBorrowedBooks
})(StudentBorrowedBooks);
