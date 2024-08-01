import React, {forwardRef, useEffect, useState} from 'react';
import * as api from "./../../../api";
import axios from "axios";
import {Modal} from 'reactstrap';
import {  Modal as Modal2} from "react-bootstrap";
import MaterialTable from 'material-table';
import {FiUploadCloud} from "react-icons/fi";
import 'semantic-ui-css/semantic.min.css';
import { Dropdown,Button as Buuton2, Menu, Icon } from 'semantic-ui-react'
import { token as token, } from "./../../../api";
import { ToastContainer, toast } from "react-toastify";
import NdrConfigurationSetup from './NdrConfigurationSetup'


import AddBox from '@material-ui/icons/AddBox';
import ArrowUpward from '@material-ui/icons/ArrowUpward';
import Check from '@material-ui/icons/Check';
import ChevronLeft from '@material-ui/icons/ChevronLeft';
import ChevronRight from '@material-ui/icons/ChevronRight';
import Clear from '@material-ui/icons/Clear';
import DeleteOutline from '@material-ui/icons/DeleteOutline';
import Edit from '@material-ui/icons/Edit';
import FilterList from '@material-ui/icons/FilterList';
import FirstPage from '@material-ui/icons/FirstPage';
import LastPage from '@material-ui/icons/LastPage';
import Remove from '@material-ui/icons/Remove';
import SaveAlt from '@material-ui/icons/SaveAlt';
import Search from '@material-ui/icons/Search';
import ViewColumn from '@material-ui/icons/ViewColumn';
import Button from '@material-ui/core/Button';


const tableIcons = {
    Add: forwardRef((props, ref) => <AddBox {...props} ref={ref}/>),
    Check: forwardRef((props, ref) => <Check {...props} ref={ref}/>),
    Clear: forwardRef((props, ref) => <Clear {...props} ref={ref}/>),
    Delete: forwardRef((props, ref) => <DeleteOutline {...props} ref={ref}/>),
    DetailPanel: forwardRef((props, ref) => <ChevronRight {...props} ref={ref}/>),
    Edit: forwardRef((props, ref) => <Edit {...props} ref={ref}/>),
    Export: forwardRef((props, ref) => <SaveAlt {...props} ref={ref}/>),
    Filter: forwardRef((props, ref) => <FilterList {...props} ref={ref}/>),
    FirstPage: forwardRef((props, ref) => <FirstPage {...props} ref={ref}/>),
    LastPage: forwardRef((props, ref) => <LastPage {...props} ref={ref}/>),
    NextPage: forwardRef((props, ref) => <ChevronRight {...props} ref={ref}/>),
    PreviousPage: forwardRef((props, ref) => <ChevronLeft {...props} ref={ref}/>),
    ResetSearch: forwardRef((props, ref) => <Clear {...props} ref={ref}/>),
    Search: forwardRef((props, ref) => <Search {...props} ref={ref}/>),
    SortArrow: forwardRef((props, ref) => <ArrowUpward {...props} ref={ref}/>),
    ThirdStateCheck: forwardRef((props, ref) => <Remove {...props} ref={ref}/>),
    ViewColumn: forwardRef((props, ref) => <ViewColumn {...props} ref={ref}/>)
};


export default function DownloadNdr() {
    const [setUpDetail, setSetUpDetail]= useState("")
    const [ndrDetail, setNdrDetail] = useState()
    const [saving, setSaving] = useState(false)
    const [modal, setModal] = useState(false);
    const toggle = () => setModal(!modal);
    const [showModal, setShowModal] = React.useState(false);
    const toggleModal = () => setShowModal(!showModal)
    useEffect(() => {
        ndrSetup()
    }, []);

    ///GET LIST OF NDR SETUP
    const ndrSetup =()=> {
       
        axios
            .get(`${api.url}ndr-emr/auto-push-configuration-viewer`,
                {headers: {"Authorization": `Bearer ${api.token}`}}
            )
            .then((response) => {
                if(response.data!=="" && response.data!==null){
                    setNdrDetail(response.data);
                }else{
                    setNdrDetail()
                }
            })
            .catch((error) => {
                ///console.log(error)
            });

    }

    const deleteAccount = () => {
        setSaving(true)
        axios
            .delete(`${api.url}ndr-emr/auto-push-configuration-deleter`,
                {headers: {"Authorization": `Bearer ${api.token}`}}
            )
            .then((response) => {
                toast.success(" Record Deleted Successful!");
                ndrSetup();
                toggle()
                setSaving(false)
            })
            .catch((error) => {
                setSaving(false)
                setModal(!modal)
            });
    }
    const  generateAction = (fileName) => {
        setModal(true)
       
         //SENDING A POST REQUEST 
         axios.POST(`${api.url}ndr-emr/ndr-auto-pusher?id=${fileName}`, fileName,
                     { headers: {"Authorization" : `Bearer ${token}`} }
                   )
                 .then(response => {
                   window.setTimeout(() => {
                     toast.success(" Generating NDR Successful!");
                     setModal(false)
                     //props.setValue(1)
                   }, 5000);
                   
                   //props.history.push("/generate", { state: 'download'});
                 })
                 .catch(error => {
                   setModal(false)
                   toast.error(" Something went wrong!");
                   if(error.response && error.response.data){
                     let errorMessage = error.response.data.apierror && error.response.data.apierror.message!=="" ? error.response.data.apierror.message :  "Something went wrong, please try again";
                     toast.error(errorMessage);
                   }
                   else{
                     toast.error("Something went wrong. Please try again...");
                   }
                 });
       }

    const setupModal =(row)=> { 
              
        setShowModal(!showModal)
        setSetUpDetail(row)
    }
    const deleteModal =()=> {        
        setModal(!modal)
    }

    
    return (
        <div>
            <ToastContainer autoClose={3000} hideProgressBar />
            {ndrDetail===undefined && (
            <Button
                variant="contained"
                color="primary"
                className=" float-right"
                startIcon={<FiUploadCloud size="10"/>}
                style={{backgroundColor: '#014d88'}}
                onClick={setupModal}
              
            >
                <span> NDR Setup</span>
            </Button>
            )}
            <br/><br/>
            <MaterialTable
                icons={tableIcons}
                title="NDR Setup Detail"

                columns={[
                    {title: "Username", field: "username", filtering: false},
                    
                    {title: "Password", field: "password", filtering: false},
                    {
                        title: "Action",
                        field: "actions",
                        filtering: false,
                    },
                ]}
                //isLoading={loading}
                data={ndrDetail &&  [ndrDetail].map((row) => ({
                    username: row.username,
                    password: row.password,
                    actions:<div>
                    <Menu.Menu position='right'  >
                    <Menu.Item >
                        <Buuton2 style={{backgroundColor:'rgb(153,46,98)'}} primary>
                        <Dropdown item text='Action'>

                        <Dropdown.Menu style={{ marginTop:"10px", }}>
                            {/* <Dropdown.Item  onClick={() =>setupModal(row)}><Edit color="primary"/> Edit
                            </Dropdown.Item> */}
                            <Dropdown.Item  onClick= {() => deleteModal(row.id)}><DeleteOutline color="primary"/> Delete
                            </Dropdown.Item>
                        </Dropdown.Menu>
                    </Dropdown>
                        </Buuton2>
                    </Menu.Item>
                    </Menu.Menu>
              </div>
                }))}
                options={{

                    pageSizeOptions: [5,],
                    headerStyle: {
                        backgroundColor: "#014d88",
                        color: "#fff",
                        margin: "auto"
                    },
                    filtering: true,
                    searchFieldStyle: {
                        width: '200%',
                        margingLeft: '250px',
                    },
                    exportButton: false,
                    searchFieldAlignment: 'left',
                }}

            />

        <Modal2 show={modal} toggle={toggle} className="fade" size="md"
            aria-labelledby="contained-modal-title-vcenter"
            centered backdrop="static">
            <Modal2.Header >
        <Modal2.Title id="contained-modal-title-vcenter">
            Notification!
        </Modal2.Title>
        </Modal2.Header>
            <Modal2.Body>
                <h4>Are you Sure you want to delete the record ?</h4>
                
            </Modal2.Body>
        <Modal2.Footer>
            <Button onClick={()=>deleteAccount()}  style={{backgroundColor:"red", color:"#fff"}} disabled={saving}>{saving===false ? "Yes": "Deleting..."}</Button>
            <Button onClick={toggle} style={{backgroundColor:"#014d88", color:"#fff"}} disabled={saving}>No</Button>
            
        </Modal2.Footer>
        </Modal2>      
            <NdrConfigurationSetup toggleModal={toggleModal} showModal={showModal} NdrSetup={ndrSetup} setUpDetail={ndrDetail}/>
        </div>

    );

}
