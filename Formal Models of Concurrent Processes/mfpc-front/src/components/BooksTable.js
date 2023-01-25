import React, {useState} from "react";
import DataTable from "react-data-table-component";
import {connect} from "react-redux";
import PropTypes from "prop-types";
import {getBooks} from "../store/book/bookActions";
import _ from "lodash";
import BookOptions from "./BookOptions";
import {Checkbox, makeStyles} from "@material-ui/core";
import Button from "@material-ui/core/Button";
import AddBookForm from "./AddBookForm";
import {getStudents} from "../store/student/studentActions";

const columns = [
    {
        name: "Name",
        selector: row => row.name,
        sortable: false,
        ignoreRowClick: true,
        width: "36%"
    },
    {
        name: "Page Count",
        selector: row => row.pageCount,
        sortable: false,
        ignoreRowClick: true,
        width: "10%"
    },
    {
        name: "Author Name",
        selector: row => row.authorName,
        sortable: false,
        ignoreRowClick: true,
        width: "36%"
    },
    {
        name: "Is borrowed?",
        selector: row => row.borrowed,
        sortable: false,
        ignoreRowClick: true,
        width: "10%"
    },
    {
        selector: row => row.options,
        sortable: false,
        center: true,
        width: "8%"
    }
];

const BooksTable = ({getBooks, loading, books, match}) => {

    const [filterParam, setfilterParam] = useState("");


    if (loading) {
        getBooks()
    }

    function onChange(event) {
        setfilterParam(event.target.value);
        getBooks(event.target.value);
    }

    const useStyles = makeStyles(() => ({
        root: {
            '&:hover': {
                backgroundColor: 'transparent !important'
            }
        }
    }));
    const classes = useStyles()
    const filterOptions = [['all', 'All'], ['borrowed', 'Borrowed'], ['not_borrowed', 'Not Borrowed']]

    const rows = _.map(books, item => {
        return {
            name: item.name,
            pageCount: item.pageCount,
            authorName: item.authorName,
            borrowed: (
                <Checkbox readOnly disableRipple style={{cursor: 'none', pointerEvents: 'none'}} color={'primary'}
                          checked={item.borrowed}
                          classes={{root: classes.root}}/>
            ),
            options: (
                <BookOptions
                    bookId={item.bookId}
                    borrowed={item.borrowed}
                />
            )
        };
    });
    return (
        <>
            <span style={{display: 'flex'}}>
                <p>Filter:&nbsp;&nbsp;</p>
            <select
                id="books"
                name="books"
                onChange={e => onChange(e)}
            >
                {filterOptions.map(fo => {
                    return (
                        <option key={fo[0]} value={fo[0]}>
                            {fo[1]}
                        </option>
                    );
                })}
            </select>
                &emsp;&emsp;&emsp;&emsp;
                <AddBookForm/>
                </span>
            <DataTable
                title="Books"
                columns={columns}
                data={rows}
            />
        </>
    );
};
BooksTable.propTypes = {
    getBooks: PropTypes.func.isRequired,
    getStudents: PropTypes.func.isRequired,
    loading: PropTypes.bool,
    books: PropTypes.array,
};

const mapStateToProps = state => ({
    books: state.books.data,
    loading: state.books.loading,
});

export default connect(mapStateToProps, {
    getBooks,
    getStudents
})(BooksTable);
