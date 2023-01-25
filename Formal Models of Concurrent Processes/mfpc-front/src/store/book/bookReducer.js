import {ADD_BOOK, BORROW_BOOK, DELETE_BOOK, GET_BOOKS, GET_REQUEST_FAIL, RETURN_BOOK} from './bookActions';

const initialState = {
    loading: true,
    data: []
};

// eslint-disable-next-line import/no-anonymous-default-export
export default function (state = initialState, action) {
    const {type, payload} = action;


    switch (type) {
        case GET_BOOKS:
            return {
                ...state,
                loading: false,
                data: payload
            };
        case BORROW_BOOK:
            return {
                ...state,
                loading: true
            }
        case RETURN_BOOK:
            return {
                ...state,
                loading: true
            }
        case DELETE_BOOK:
            return {
                ...state,
                loading: true
            }
        case ADD_BOOK:
            return {
                ...state,
                loading: true
            }
        case GET_REQUEST_FAIL:
        default:
            return state;

    }
}
