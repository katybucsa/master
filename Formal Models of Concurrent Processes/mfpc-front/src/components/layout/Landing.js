import React from "react";
import {Navigate} from 'react-router-dom';
import {connect} from "react-redux";

const Landing = () => {
    return <Navigate to="/books"/>;
};

Landing.propTypes = {};

const mapStateToProps = () => ({});

export default connect(mapStateToProps)(Landing);
