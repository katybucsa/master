import axios from "axios";
import {setAlert} from "../alert/alertActions";

export const GET_BOOKS = "GET_BOOKS";
export const ADD_BOOK = "ADD_BOOK";
export const BORROW_BOOK = "BORROW_BOOK";
export const RETURN_BOOK = "RETURN_BOOK";
export const DELETE_BOOK = "DELETE_BOOK";
export const GET_REQUEST_FAIL = "GET_REQUEST_FAIL";

export const getBooks = (filter = 'all') => async dispatch => {
    try {
        axios.get(`/books?filter=${filter}`).then(res => {
                dispatch({
                    type: GET_BOOKS,
                    payload: res.data.data
                })
            }
        );

    } catch (err) {
        const status = err.response.status;
        if (status === 400) {
            dispatch(setAlert("Try again", "danger"));
        }
        dispatch({
            type: GET_REQUEST_FAIL,
            payload: err
        });
    }
};

export const addBook = (name, pageCount, authorId) => async dispatch => {

    const body = {
        name: name,
        pageCount: pageCount,
        authorId: authorId
    };
    try {
        axios.post('/books', body).then(res => {
                dispatch({
                    type: ADD_BOOK,
                    payload: res.data
                })
            }
        );

    } catch (err) {
        const status = err.response.status;
        if (status === 400) {
            dispatch(setAlert("Try again", "danger"));
        }
        dispatch({
            type: GET_REQUEST_FAIL,
            payload: err
        });
    }

};

export const borrowBook = (bookId, studentId) => async dispatch => {

    const body = {
        bookId: bookId,
        studentId: studentId
    };
    try {
        axios.post('/borrows', body).then(res => {
                dispatch({
                    type: BORROW_BOOK,
                    payload: res.data
                })
            }
        );

    } catch (err) {
        const status = err.response.status;
        if (status === 400) {
            dispatch(setAlert("Try again", "danger"));
        }
        dispatch({
            type: GET_REQUEST_FAIL,
            payload: err
        });
    }
};

export const returnBook = (bookId) => async dispatch => {

    try {
        axios.put(`/books/returns/${bookId}`).then(res => {
                dispatch({
                    type: RETURN_BOOK,
                    payload: res.data
                })
            }
        );

    } catch (err) {
        const status = err.response.status;
        if (status === 400) {
            dispatch(setAlert("Try again", "danger"));
        }
        dispatch({
            type: GET_REQUEST_FAIL,
            payload: err
        });
    }
};

export const deleteBook = (bookId) => async dispatch => {

    try {
        axios.delete(`/books/${bookId}`).then(res => {
                dispatch({
                    type: DELETE_BOOK,
                    payload: res.data
                })
            }
        );

    } catch (err) {
        const status = err.response.status;
        if (status === 400) {
            dispatch(setAlert("Try again", "danger"));
        }
        dispatch({
            type: GET_REQUEST_FAIL,
            payload: err
        });
    }
}
