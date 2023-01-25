import React, {Fragment} from "react";
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";

// Redux
import {Provider} from "react-redux";
import {ToastContainer} from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "./App.css";
import {store} from "./store";
import BooksTable from "./components/BooksTable";
import Navbar from "./components/layout/navbar/Navbar";
import NotFound from "./components/layout/NotFound";
import Alert from "./components/layout/Alert";
import StudentsTable from "./components/StudentsTable";
import StudentBorrowedBooks from "./components/StudentBorrowedBooks";

function App() {
    return (
        <Provider store={store}>
            <Router>
                <Fragment>
                    <Navbar/>
                    <section className="container">
                        <Alert/>
                        <Routes>
                            <Route exact path="/" element={<BooksTable/>}/>
                            <Route exact path='/books' element={<BooksTable/>}/>
                            <Route exact path='/students' element={<StudentsTable/>}/>
                            <Route exact path='/books/student/:sId' element={<StudentBorrowedBooks/>}/>
                            <Route component={NotFound}/>
                        </Routes>
                        <ToastContainer/>
                    </section>
                </Fragment>
            </Router>
        </Provider>
    );
}

export default App;
