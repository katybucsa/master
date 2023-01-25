import React from "react";
import {Routes, Route} from "react-router-dom";
import Alert from "../layout/Alert";
import BooksTable from "../BooksTable"
import NotFound from "../layout/NotFound";

const AppRoutes = () => {
    return (
        <section className="container">
            <Alert/>
            <Routes>
                <Route exact path='/books' element={<BooksTable/>} />
                <Route component={NotFound}/>
            </Routes>
        </section>
    );
};

export default AppRoutes;
