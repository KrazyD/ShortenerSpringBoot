import React, {Component} from 'react';
import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';

import {ContextMenu} from 'primereact/contextmenu';
import {Button} from 'primereact/button';
import RefWebService from '../webService/RefWebService';
import RefDialog from './RefDialog';

export default class RefList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            refs: [],
            selectedRef: {fullRef: '', reducedRef: '', requestsNumb: 0, userId: 0},
            isDialogDisplay: false
        };

        let currentUser = this.props?.location?.state?.currentUser;
        if (currentUser) {
            this.userId = currentUser.hasOwnProperty('id') ? currentUser.id : 0;
            this.isCurUserAdmin = currentUser.roles?.includes('ROLE_ADMIN');
        } else {
            this.userId = 0;
            this.isCurUserAdmin = false;
        }

        this.menu = [
            {label: 'Add', icon: 'pi pi-fw pi-plus', command: () => this.onRowAdd()},
            {label: 'Edit', icon: 'pi pi-fw pi-pencil', command: () => this.onRowEdit(this.state.selectedRef)},
            {label: 'Delete', icon: 'pi pi-fw pi-trash', command: () => this.onRowDelete(this.state.selectedRef)}
        ];

        this.onRowAdd = this.onRowAdd.bind(this);
        this.onRowEdit = this.onRowEdit.bind(this);
        this.onRowDelete = this.onRowDelete.bind(this);
    }

    componentDidMount() {
        if (this.isCurUserAdmin) {
            RefWebService.getRefs().then(response => {
                this.setState({refs: response.data});
            }, error => {
                this.props.getGrowl().show({severity: 'error', summary: error.status, detail: error.message});
            });
        } else {
            RefWebService.getUserRefs(this.userId).then(response => {
                this.setState({refs: response.data});
            }, error => {
                this.props.getGrowl().show({severity: 'error', summary: error.status, detail: error.message});
            });
        }

    }

    onRowAdd() {
        this.setState({
            selectedRef:  {fullRef: '', reducedRef: '', requestsNumb: 0, userId: 0},
            isDialogDisplay: true
        });
    }

    onRowEdit(selectedRef) {
        this.setState({
            selectedRef: selectedRef,
            isDialogDisplay: true
        });
    }

    onRowDelete(selectedRef) {
        RefWebService.deleteRef(selectedRef).then(response => {
            let index = this.state.refs.indexOf(selectedRef);
            this.setState({refs: this.state.refs.filter((val, i) => i !== index)});
            this.props.getGrowl().show({severity: 'success', summary: response.status, detail: response.data});
        }, error => {
            this.props.getGrowl().show({severity: 'error', summary: error.status, detail: error.message});
        });
    }

    handleRefFromDialog = (ref, isNew) => {
        if (ref) {
            if (isNew) {
                if (this.userId && this.userId !== 0) {
                    RefWebService.createRef(ref, this.userId).then(response => {
                        let refs = [...this.state.refs];
                        refs.push(response.data);
                        this.setState({
                            refs: refs,
                            isDialogDisplay: false
                        });
                        this.props.getGrowl().show({severity: 'success', summary: 'Success', detail: 'Reference is created'});
                    }, error => {
                        this.setState({isDialogDisplay: false});
                        this.props.getGrowl().show({severity: 'error', summary: error.status, detail: error.message});
                    });
                } else {
                    this.setState({isDialogDisplay: false});
                    this.props.getGrowl().show({severity: 'error', summary: 'Error', detail: 'ID of current user undefined'});
                }
            } else {
                RefWebService.updateRef(ref).then(response => {
                    let refs = [...this.state.refs];
                    let index = refs.findIndex((item) => item.id === response.data.id);
                    refs[index] = Object.assign({}, response.data);
                    this.setState({
                        isDialogDisplay: false,
                        refs: refs});
                    this.props.getGrowl().show({severity: 'success', summary: 'Success', detail: 'Reference is updated'});
                }, error => {
                    this.setState({isDialogDisplay: false});
                    this.props.getGrowl().show({severity: 'error', summary: error.status, detail: error.message});
                });
            }
        } else {
            this.setState({
                isDialogDisplay: false
            });
        }
    };

    render() {
        let footer = <div className="p-clearfix" >
                         <Button label="Add" icon="pi pi-plus" onClick={this.onRowAdd}/>
                     </div>;

        return (
            <div>
                <ContextMenu model={this.menu} ref={el => this.cm = el} />

                <DataTable value={this.state.refs} editMode="row" footer={footer} header="Reference list"
                           contextMenuSelection={(e) => this.state.selectedRef}
                           onContextMenuSelectionChange={e => this.setState({selectedRef: e.value})}
                           onContextMenu={e => this.cm.show(e.originalEvent)}>
                    <Column className='overflow-text' field="id" header="Reference Id" style={{width: '8%'}}/>
                    <Column className='overflow-text' field="fullRef" header="Full Reference" style={{width: '60%'}}/>
                    <Column className='overflow-text' field="reducedRef" header="Reduced Reference" style={{width: '14%'}}/>
                    <Column className='overflow-text' field="requestsNumb" header="Requests Number" style={{width: '10%'}}/>
                    <Column className='overflow-text' field="userId" header="User Id" style={{width: '8%'}}/>
                </DataTable>
                <RefDialog isDialogDisplay={this.state.isDialogDisplay}
                            onChangeFinish={this.handleRefFromDialog}
                            reference={Object.assign({}, this.state.selectedRef)} />
            </div>
        )
    }
};