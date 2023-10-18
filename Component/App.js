import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Login from './Login.js'
import Register from './Register.js';

function App() {
  return (
    <div>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login/>} />
          <Route path="/api/v1/login" element={<Login/>} />
          <Route path="/api/v1/join" element={<Register/>} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
