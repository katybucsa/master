import React from "react";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import IconButton from "@material-ui/core/IconButton";
import MoreVertIcon from "@material-ui/icons/MoreVert";
import {useDispatch} from "react-redux";
import {deleteStudent, getStudents} from "../store/student/studentActions";
import {Link} from "react-router-dom";

const StudentOptions = ({studentId, canDelete}) => {
    const [anchorEl, setAnchorEl] = React.useState(null);
    const handleClick = event => {
        setAnchorEl(event.currentTarget);
    };

    const dispatch = useDispatch();
    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleDeleteStudent = () => {
        handleClose();
        dispatch(deleteStudent(studentId));
    }

    return (
        <>
            <IconButton onClick={handleClick}>
                <MoreVertIcon/>
            </IconButton>
            <Menu
                id="simple-menu"
                anchorEl={anchorEl}
                keepMounted
                open={Boolean(anchorEl)}
                onClose={handleClose}
            >
                <MenuItem disabled={!canDelete} onClick={handleDeleteStudent}>
                    Delete student
                </MenuItem>
                <MenuItem>
                    <Link to={`/books/student/${studentId}`}>Books Page</Link>
                </MenuItem>
            </Menu>
        </>
    );
};

export default StudentOptions;
