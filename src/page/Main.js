import React, { useState, useEffect }  from 'react'
import KakaoLoginPage from '../component/KakaoLoginPage';
import { useNavigate } from 'react-router-dom';

const Main = () => {
    const [showButtons, setShowButtons] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const timer = setTimeout(() => {
          setShowButtons(true);
        }, 2000);
        return () => clearTimeout(timer);
      }, []);


      const joinMembership = () => {
        navigate('/Login');
      }

  return (
     <div className="Main">
      <div style={{ textAlign: 'center' }}>
        <img className="MainLogo" src="img/Rectangle.jpg" />
        <p>여기에 작은 글귀</p>
      </div>
      {showButtons && (
        <div style={{ textAlign: 'center' }}>
          <button onClick={joinMembership}>로그인</button>
          <KakaoLoginPage></KakaoLoginPage>
        </div>
      )}
    </div>

  )
}

export default Main