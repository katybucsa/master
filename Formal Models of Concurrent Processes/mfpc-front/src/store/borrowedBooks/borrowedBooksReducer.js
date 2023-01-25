import {GET_REQUEST_FAIL, GET_STUDENT_BORROWED_BOOKS} from "./borrowedBooksActions";

const initialState = {
    loading: true,
    data: []
};

// eslint-disable-next-line import/no-anonymous-default-export
export default function (state = initialState, action) {
    const {type, payload} = action;


    switch (type) {
        case GET_STUDENT_BORROWED_BOOKS:
            return {
                ...state,
                loading: false,
                data: payload
            }
        case GET_REQUEST_FAIL:
        default:
            return state;

    }
}
