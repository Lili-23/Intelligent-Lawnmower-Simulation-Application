
import React, { Component } from 'react';
import { Segment, SegmentGroup, Container } from 'semantic-ui-react';

class MowerInfo extends Component {
    constructor(props) {
        super(props);
        this.state = {}
    }

    render() {
        
        return (
            <Container>
                {this.props.mowersInfo.map((mower, index) => {
                    const info = mower[1].split(",");
                    const energy = info[0];
                    const direction = info[1];
                    return(
                        <SegmentGroup horizontal>
                            <Segment size="small">Mower ID: {index}</Segment>
                            <Segment size="small">Mower state: {mower[0]}</Segment>
                            <Segment size="small">Mower Energy: {energy}</Segment>
                            <Segment size="small">Mower Direction: {direction}</Segment>
                        </SegmentGroup>
                    
                    )
                })}
            </Container>
            
            
        )
    }
}
export default MowerInfo;