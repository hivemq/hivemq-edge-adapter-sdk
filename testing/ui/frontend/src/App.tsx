import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { MainPage } from './pages/MainPage'
import { FormPage } from './pages/FormPage'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/form" element={<FormPage />} />
        <Route path="/form/:id" element={<FormPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
