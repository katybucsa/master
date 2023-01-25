import {applyMiddleware, combineReducers, createStore} from "redux";
import alert from "./alert/alertReducer"
import thunk from 'redux-thunk';
import students from "./student/studentReducer";
import books from "./book/bookReducer";
import authors from "./authors/authorReducer";
import borrowedBooks from "./borrowedBooks/borrowedBooksReducer";

export const store = createStore(combineReducers({
    alert,
    students,
    books,
    authors,
    borrowedBooks
}), applyMiddleware(thunk));
