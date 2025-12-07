import React, { useState, useEffect } from 'react';
import './App.css';

const API_URL = "http://localhost:8080/api";

function App() {
  const [activeTab, setActiveTab] = useState(0);
  const [logs, setLogs] = useState(["> Waiting for Java Backend connection..."]);
  
  const [graphData, setGraphData] = useState({ nodes: [], links: [] });
  const [path, setPath] = useState([]);
  
  const [testCase, setTestCase] = useState(1);
  const [selectedStudent, setSelectedStudent] = useState("");
  const [targetCo, setTargetCo] = useState("Google");
  const [studentDetails, setStudentDetails] = useState(null);

  const loadData = async (id) => {
    try {
      setLogs(prev => [...prev, `> Requesting Test Case ${id} from Server...`]);
      const res = await fetch(`${API_URL}/load/${id}`);
      const data = await res.json();
      
      setGraphData(data);
      setPath([]); 
      setLogs(prev => [...prev, `> Success: ${data.status}`]);
      setLogs(prev => [...prev, `> Loaded ${data.nodes.length} students.`]);
    } catch (e) {
      console.log(e);
      setLogs(prev => [...prev, `> ERROR: Could not connect to Java Server. Is it running?`]);
    }
  };

  const assignRoommates = async () => {
    try {
      const res = await fetch(`${API_URL}/roommates`, { method: 'POST' });
      const data = await res.json();
      setGraphData(data);
    } catch (e) { alert("Error assigning roommates"); }
  };

  const findPath = async () => {
    if(!selectedStudent) return;
    try {
      const res = await fetch(`${API_URL}/path?start=${selectedStudent}&targetCo=${targetCo}`);
      const pathList = await res.json();
      setPath(pathList);
      if(pathList.length === 0) alert("No path found.");
    } catch (e) { alert("Error finding path"); }
  };

  const fetchDetails = async (name) => {
    if(!name) return;
    setSelectedStudent(name);
    try {
      const res = await fetch(`${API_URL}/student/${name}`);
      const data = await res.json();
      setStudentDetails(data);
    } catch (e) { console.error(e); }
  };

  return (
    <div className="container">
      <div className="header">
        <h1 style={{color: '#BF5700'}}>Longhorn Network Lab (Spring + React)</h1>
      </div>

      <div className="tab-nav">
        {["Load Data", "Student Graph", "Details"].map((t, i) => (
          <button key={i} className={`tab-btn ${activeTab === i ? 'active' : ''}`} onClick={() => setActiveTab(i)}>
            {t}
          </button>
        ))}
      </div>

      <div className="content">
        {/* TAB 1: LOAD */}
        {activeTab === 0 && (
          <div className="panel">
            <div className="sidebar">
              <h3>Backend Controls</h3>
              <select onChange={(e) => setTestCase(e.target.value)} value={testCase}>
                <option value="1">Test Case 1</option>
                <option value="2">Test Case 2</option>
                <option value="3">Test Case 3</option>
              </select>
              <button className="btn" onClick={() => loadData(testCase)}>Load from Java</button>
            </div>
            <div className="graph-area" style={{background: '#1e1e1e', border: 'none'}}>
              <div className="console">
                {logs.map((l, i) => <div key={i}>{l}</div>)}
              </div>
            </div>
          </div>
        )}

        {/* TAB 2: GRAPH */}
        {activeTab === 1 && (
          <div className="panel">
            <div className="sidebar">
              <h3>Graph Controls</h3>
              <button className="btn" onClick={() => loadData(testCase)}>Refresh Graph</button>
              <hr/>
              <button className="btn" onClick={assignRoommates}>Run Roommate Algo</button>
              <hr/>
              <label>Start Node:</label>
              <select onChange={(e) => setSelectedStudent(e.target.value)} value={selectedStudent}>
                <option value="">Select...</option>
                {graphData.nodes.map(n => <option key={n.id} value={n.id}>{n.id}</option>)}
              </select>
              <label>Target Company:</label>
              <input value={targetCo} onChange={(e) => setTargetCo(e.target.value)} />
              <button className="btn" onClick={findPath} style={{marginTop:'10px'}}>Find Path</button>
            </div>
            <div className="graph-area">
              <GraphVisualizer data={graphData} path={path} />
            </div>
          </div>
        )}

        {/* TAB 3: DETAILS */}
        {activeTab === 2 && (
          <div className="panel">
             <div className="sidebar">
              <h3>Select Student</h3>
              <select onChange={(e) => fetchDetails(e.target.value)} value={selectedStudent}>
                <option value="">Select...</option>
                {graphData.nodes.map(n => <option key={n.id} value={n.id}>{n.id}</option>)}
              </select>
            </div>
            <div style={{padding: '20px'}}>
              {studentDetails ? (
                <div>
                   <h2 style={{color:'#BF5700'}}>{studentDetails.name}</h2>
                   <h4>Friend Requests:</h4>
                   <p>{studentDetails.requests && studentDetails.requests.length ? studentDetails.requests.join(", ") : "None"}</p>
                   <h4>Chat History:</h4>
                   <pre style={{background:'#eee', padding:'10px'}}>{studentDetails.chats && studentDetails.chats.length ? studentDetails.chats.join("\n") : "None"}</pre>
                </div>
              ) : <p>Select a student to view live data from server.</p>}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

const GraphVisualizer = ({ data, path }) => {
  if(!data.nodes.length) return <div>No Data Loaded</div>;
  const width = 600, height = 500, cx = width/2, cy = height/2, r = 150;
  
  const coords = {};
  data.nodes.forEach((n, i) => {
    const angle = (2 * Math.PI * i) / data.nodes.length;
    coords[n.id] = { x: cx + r * Math.cos(angle), y: cy + r * Math.sin(angle) };
  });

  return (
    <svg width="100%" height="100%" viewBox={`0 0 ${width} ${height}`}>
      {/* Links (Gray) */}
      {data.links.map((l, i) => (
        <line key={i} x1={coords[l.source].x} y1={coords[l.source].y} x2={coords[l.target].x} y2={coords[l.target].y} stroke="#ccc" strokeWidth="1" />
      ))}
      {/* Roommates (Blue) */}
      {data.nodes.filter(n => n.roommate && n.id < n.roommate).map(n => (
        <line key={`rm-${n.id}`} x1={coords[n.id].x} y1={coords[n.id].y} x2={coords[n.roommate].x} y2={coords[n.roommate].y} stroke="blue" strokeWidth="3" />
      ))}
      {/* Path (Green) */}
      {path.length > 1 && path.map((name, i) => {
        if(i === path.length - 1) return null;
        return <line key={`path-${i}`} x1={coords[name].x} y1={coords[name].y} x2={coords[path[i+1]].x} y2={coords[path[i+1]].y} stroke="#00b400" strokeWidth="4" />;
      })}
      {/* Nodes */}
      {data.nodes.map(n => (
        <g key={n.id}>
          <circle cx={coords[n.id].x} cy={coords[n.id].y} r="20" fill={path.includes(n.id) ? "#00b400" : (n.roommate ? "blue" : "#BF5700")} />
          <text x={coords[n.id].x} y={coords[n.id].y + 5} textAnchor="middle" fill="white" fontWeight="bold">{n.id.charAt(0)}</text>
          <text x={coords[n.id].x} y={coords[n.id].y - 25} textAnchor="middle" fill="black">{n.id}</text>
        </g>
      ))}
    </svg>
  );
};

export default App;
