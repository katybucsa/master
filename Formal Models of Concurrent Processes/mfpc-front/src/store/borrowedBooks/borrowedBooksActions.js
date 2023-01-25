import axios from "axios";
import {setAlert} from "../alert/alertActions";

export const GET_STUDENT_BORROWED_BOOKS = "GET_STUDENT_BORROWED_BOOKS";
export const GET_REQUEST_FAIL = "GET_REQUEST_FAIL";

export const getStudentBorrowedBooks = (sId, history) => async dispatch => {
    try {
        axios.get(`/books/student/${sId}`).then(res => {
                dispatch({
                    type: GET_STUDENT_BORROWED_BOOKS,
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
        history.push('/students');
    }
};
