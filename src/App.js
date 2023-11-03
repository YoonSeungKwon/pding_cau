// import * as React from "react";
import React, { useEffect, Component} from 'react';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import {Routes, Route} from "react-router-dom";
import FriendsList from './page/FriendsList';
import ProductDetail from './page/ProductDetail';
import Main from './page/Main';
import Basket from './page/Basket';
import Login from './page/Login';
import Signup from "./page/Signup";
import WritePost from "./page/WritePost";
import PropTypes from 'prop-types';


function App() {

  useEffect(() => {
    // 로컬 스토리지에서 토큰 가져오기
    const token = localStorage.getItem('token');
    if (token) {
      // 토큰이 존재한다면 로그인 상태 유지 처리
      // dispatch(userActions.loginCheckDB());
    }
  }, []);

  return (
    <div >
      <Routes>
        <Route path="/" element={<Main/>}/>
        <Route path="/basket/:id" element={<Basket/>}/>
        <Route path="/list/:id" element={<FriendsList/>}/>
        <Route path="/details/:id" element={<ProductDetail/>}/>
        <Route path="/login" element={<Login/>}/>
        <Route path="/signUp" element={<Signup/>}/>
        <Route path="/write" element={<WritePost/>}/>
      </Routes>
     
    </div>
  );
}

export default App;
