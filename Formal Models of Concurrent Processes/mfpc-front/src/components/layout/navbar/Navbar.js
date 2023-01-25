import React, {Fragment} from "react";
import {Link} from "react-router-dom";
// import NavbarMenu from "./NavbarMenu";
import AddAuthorForm from "../../AddAuthorForm";

const Navbar = () => {
    const links = (
        <ul>
            <li>
                <Link to="/books">Books Page</Link>
            </li>

            <li>
                <Link to="/students">
                    <i className="fas fa-user"/>{" "}
                    <span className="hide-sm">Students Page</span>
                </Link>
            </li>
            <li>
                <AddAuthorForm/>
            </li>
        </ul>
    );
    return (
        <nav className="navbar bg-dark">
            <h1>
                <Link to="/">
                    <i className="fas fa-code"/> University Library
                </Link>
            </h1>
            <Fragment>{links}</Fragment>
            {/*<NavbarMenu/>*/}
            {/*<ul className="menus">*/}
            {/*    {menuItems.map((menu, index) => {*/}
            {/*        const depthLevel = 0;*/}
            {/*        return <MenuItems items={menu} key={index} depthLevel={depthLevel} />;*/}
            {/*    })}*/}
            {/*</ul>*/}
            {/*<NavbarDropdown>*/}
            {/*    <NavbarDropdown.Toggle className="menu__item">*/}
            {/*        <NavbarDropdown.Open>*/}
            {/*            <FontAwesomeIcon icon={faTh} fixedWidth/>*/}
            {/*        </NavbarDropdown.Open>*/}
            {/*        <NavbarDropdown.Close>*/}
            {/*            <FontAwesomeIcon icon={faTimes} fixedWidth/>*/}
            {/*        </NavbarDropdown.Close>*/}
            {/*    </NavbarDropdown.Toggle>*/}
            {/*    <NavbarDropdown.Menu className="example2-dropdown-menu">*/}
            {/*        <NavbarDropdown.Item className="example2-dropdown-menu-item">*/}
            {/*            <Link to="/books">*/}
            {/*                <div className="example2-dropdown-menu-item__text">Books Page</div>*/}
            {/*            </Link>*/}
            {/*        </NavbarDropdown.Item>*/}
            {/*        <NavbarDropdown.Item className="example2-dropdown-menu-item">*/}
            {/*            <Link to="/students">*/}
            {/*                <i className="fas fa-user"/>{" "}*/}
            {/*                /!*<span className="hide-sm">Students Page</span>*!/*/}
            {/*                <span className="example2-dropdown-menu-item__text">Students Page</span>*/}
            {/*            </Link>*/}
            {/*        </NavbarDropdown.Item>*/}
            {/*        /!*<div className="example2-dropdown-menu-item__text">*!/*/}
            {/*        /!*<AddAuthorForm className="example2-dropdown-menu-item"/>*!/*/}
            {/*        /!*</div>*!/*/}
            {/*    </NavbarDropdown.Menu>*/}
            {/*</NavbarDropdown>*/}
            {/*<AddAuthorForm className="example2-dropdown-menu-item"/>*/}
        </nav>
    );
};

export default Navbar;
